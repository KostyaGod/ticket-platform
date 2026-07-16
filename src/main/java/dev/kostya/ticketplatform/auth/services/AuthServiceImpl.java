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
        User founded = repository.findByUsername(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), founded.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (founded.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException();
        }

        return createSessionWithTokens(founded);
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
        UserPrincipal userPrincipal = mapper.toPrincipal(
                repository.findById(userId)
                        .orElseThrow(InvalidCredentialsException::new)
        );

        updateSecurityContextHolder(userPrincipal);
        UUID newJti = UUID.randomUUID();
        sessionService.updateJTI(sessionId, newJti);

        return new Tokens(
                jwtService.generateAccessToken(userPrincipal, sessionId),
                jwtService.generateRefreshToken(userPrincipal, sessionId, newJti)
        );
    }

    private Tokens createSessionWithTokens(User saved) {
        UserPrincipal userPrincipal = mapper.toPrincipal(saved);
        updateSecurityContextHolder(userPrincipal);

        Session session = sessionService.createSession(userPrincipal);
        UUID jti = UUID.randomUUID();
        session.setJti(jti);
        sessionService.save(session);

        return new Tokens(
                jwtService.generateAccessToken(userPrincipal, session.getSessionId()),
                jwtService.generateRefreshToken(userPrincipal, session.getSessionId(), jti)
        );
    }

    private void updateSecurityContextHolder(UserPrincipal userPrincipal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities()
                )
        );
    }
}
