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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Tokens login(LoginRequest request) {
        String login = request.login();

        Optional<User> foundOpt =
                login.contains("@")
                        ? repository.findByEmail(login)
                        : repository.findByUsername(login);

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

    @Override
    public Tokens register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (repository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = mapper.toEntity(request);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User saved = repository.save(user);

        return createSessionWithTokens(saved);
    }

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
