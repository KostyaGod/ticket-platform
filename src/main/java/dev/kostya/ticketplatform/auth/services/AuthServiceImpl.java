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

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException();
        }

        return updateSecurityContextAndCreateSessionWithTokens(user);
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
        user.setRole(Role.ROLE_USER);
        user.setStatus(UserStatus.ACTIVE);

        User saved = repository.save(user);

        return updateSecurityContextAndCreateSessionWithTokens(saved);
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
        UserPrincipal userPrincipal = mapper.toPrincipal(
                repository.findById(userId)
                        .orElseThrow(InvalidCredentialsException::new)
        );

        updateSecurityContext(userPrincipal);
        UUID newJti = sessionService.rotateJTI(sessionId);


        return new Tokens(
                jwtService.generateAccessToken(userPrincipal, sessionId),
                jwtService.generateRefreshToken(userPrincipal, sessionId, newJti)
        );
    }

    @Override
    public void logout(String refreshToken) {
        UUID oldJti = jwtService.extractJTI(refreshToken);
        UUID sessionId = jwtService.extractSessionIdRefresh(refreshToken);
        if (!sessionService.validate(sessionId, oldJti)) {
            throw new InvalidCredentialsException();
        }

        sessionService.deleteSession(sessionId);
    }

    private Tokens updateSecurityContextAndCreateSessionWithTokens(User saved) {
        UserPrincipal userPrincipal = mapper.toPrincipal(saved);
        updateSecurityContext(userPrincipal);

        Session session = sessionService.createSession(userPrincipal);

        return new Tokens(
                jwtService.generateAccessToken(
                        userPrincipal,
                        session.getSessionId()
                ),
                jwtService.generateRefreshToken(
                        userPrincipal,
                        session.getSessionId(),
                        session.getJti()
                )
        );
    }

    private void updateSecurityContext(UserPrincipal userPrincipal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities()
                )
        );
    }
}
