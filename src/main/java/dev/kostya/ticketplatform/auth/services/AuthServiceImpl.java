package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.LoginRequest;
import dev.kostya.ticketplatform.auth.dto.request.RegisterRequest;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import dev.kostya.ticketplatform.auth.exception.EmailAlreadyExistsException;
import dev.kostya.ticketplatform.auth.exception.InvalidCredentialsException;
import dev.kostya.ticketplatform.auth.exception.UserBlockedException;
import dev.kostya.ticketplatform.auth.exception.UsernameAlreadyExistsException;
import dev.kostya.ticketplatform.auth.mapper.UserMapper;
import dev.kostya.ticketplatform.auth.repositories.UserRepository;
import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.Tokens;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса аутентификации и авторизации пользователей.
 * <p>
 * Отвечает за:
 * <ul>
 *   <li>Вход пользователя по логину/email с проверкой хеша пароля и статуса учетной записи.</li>
 *   <li>Регистрацию новых пользователей с проверкой уникальности username/email и хешированием пароля BCrypt.</li>
 *   <li>Безопасную ротацию токенов (Refresh Flow) с защитой от Replay-атак через сверку JTI в Redis.</li>
 *   <li>Завершение сеанса (Logout) с удалением сессии из хранилища.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Выполняет аутентификацию пользователя.
     * <p>
     * 1. Определяет тип логина (email или username) по наличию символа '@'.<br>
     * 2. Ищет активного (неудаленного) пользователя в БД.<br>
     * 3. Сверяет пароль через {@link PasswordEncoder#matches}.<br>
     * 4. Проверяет статус пользователя на предмет блокировки ({@link UserStatus#BLOCKED}).<br>
     * 5. Создает новую сессию в Redis и возвращает пару Access/Refresh токенов.
     *
     * @param request запрос на вход (логин/email и пароль)
     * @return пара JWT токенов
     * @throws InvalidCredentialsException если логин или пароль не совпадают
     * @throws UserBlockedException        если учетная запись пользователя заблокирована
     */
    @Override
    public Tokens login(LoginRequest request) {
        String login = request.login();

        Optional<User> foundOpt =
                login.contains("@")
                        ? repository.findByEmailAndDeletedAtIsNull(login)
                        : repository.findByUsernameAndDeletedAtIsNull(login);

        User user = foundOpt.orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException();
        }

        if (user.getUserStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException();
        }

        return createSessionWithTokens(user);
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Проверяет уникальность email и username среди неудаленных пользователей,
     * шифрует пароль алгоритмом BCrypt, сохраняет пользователя в БД и инициирует новую сессию.
     *
     * @param request данные для регистрации нового пользователя
     * @return пара выданных JWT токенов
     * @throws EmailAlreadyExistsException    если email уже занят
     * @throws UsernameAlreadyExistsException если username уже занят
     */
    @Override
    public Tokens register(RegisterRequest request) {
        if (repository.existsByEmailAndDeletedAtIsNull(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (repository.existsByUsernameAndDeletedAtIsNull(request.username())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = mapper.toEntity(request);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User saved = repository.save(user);

        return createSessionWithTokens(saved);
    }

    /**
     * Выполняет безопасное обновление токенов доступа по Refresh токену.
     * <p>
     * Алгоритм ротации:
     * <ol>
     *   <li>Валидирует подпись и срок действия переданного токена.</li>
     *   <li>Извлекает из токена {@code oldJti} и {@code sessionId}.</li>
     *   <li>Проверяет в Redis, совпадает ли {@code oldJti} с текущим сохраненным JTI сессии.
     *       Если JTI не совпадает (попытка повторного использования токена) — запрос отклоняется.</li>
     *   <li>Извлекает пользователя и генерирует новый случайный {@code newJti}.</li>
     *   <li>Атомарно перезаписывает JTI сессии в Redis и выпускает новую пару токенов.</li>
     * </ol>
     *
     * @param refreshToken строка JWT Refresh токена
     * @return новая пара JWT токенов
     * @throws InvalidCredentialsException если токен невалиден или сессия отозвана
     */
    @Override
    public Tokens refresh(String refreshToken) {
        if (!jwtService.validateRefresh(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        UUID oldJti = jwtService.extractJTI(refreshToken);
        UUID sessionId = jwtService.extractSessionIdRefresh(refreshToken);

        if (!sessionService.validate(sessionId, oldJti)) {
            throw new InvalidCredentialsException();
        }

        Long userId = jwtService.extractUserIdRefresh(refreshToken);
        User user = repository.findById(userId)
                .orElseThrow(InvalidCredentialsException::new);

        UserPrincipal principal = authenticate(user);
        UUID newJti = sessionService.rotateJTI(sessionId);

        return jwtService.generateTokenPair(
                principal,
                sessionId,
                newJti
        );
    }

    @Override
    public void logout(String refreshToken) {
        UUID jti = jwtService.extractJTI(refreshToken);
        UUID sessionId = jwtService.extractSessionIdRefresh(refreshToken);
        if (!sessionService.validate(sessionId, jti)) {
            throw new InvalidCredentialsException();
        }

        Long userId = jwtService.extractUserIdRefresh(refreshToken);

        sessionService.deleteSession(sessionId, userId);
    }

    private Tokens createSessionWithTokens(User saved) {
        UserPrincipal principal = authenticate(saved);
        Session session = sessionService.createSession(principal);

        return jwtService.generateTokenPair(
                principal,
                session.getSessionId(),
                session.getJti()
        );
    }

    private UserPrincipal authenticate(User user) {
        UserPrincipal principal = mapper.toPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                )
        );

        return principal;
    }
}
