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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private SessionService sessionService;
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    private final Long id = 1L;
    private final String username = "testuser";
    private final String email = "test@test.com";
    private final String password = "password";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";


    private final RegisterRequest request = new RegisterRequest(
            username,
            email,
            password
    );

    private final User user = User.builder()
            .username(username)
            .email(email)
            .build();

    private final User savedUser = User.builder()
            .id(id)
            .username(username)
            .email(email)
            .passwordHash("encoded-password")
            .role(Role.ROLE_CLIENT)
            .status(UserStatus.ACTIVE)
            .build();

    private final UserPrincipal userPrincipal = new UserPrincipal(id, Role.ROLE_CLIENT);

    private static Stream<LoginRequest> loginRequests() {
        return Stream.of(
                new LoginRequest("test@test.com", "password"),
                new LoginRequest("testuser", "password")
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @MethodSource("loginRequests")
    void login_success(LoginRequest request) {
        configureAndCreateLoginRequest(request, Optional.of(savedUser));

        when(passwordEncoder.matches(
                request.password(),
                savedUser.getPasswordHash()
        )).thenReturn(true);

        Session session = new Session(
                UUID.randomUUID(),
                savedUser.getId(),
                UUID.randomUUID(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 60_000
        );

        Tokens tokens = new Tokens(accessToken, refreshToken);

        configureCreateSessionWithTokens(
                savedUser,
                userPrincipal,
                session,
                tokens
        );

        Tokens result = authService.login(request);

        assertSame(tokens, result);

        verify(sessionService).createSession(userPrincipal);

        assertAuthentication(userPrincipal);
    }

    @ParameterizedTest
    @MethodSource("loginRequests")
    void login_shouldThrowException_whenUserNotFound(LoginRequest request) {
        configureAndCreateLoginRequest(request, Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(
                passwordEncoder,
                mapper,
                sessionService,
                jwtService
        );
    }

    @ParameterizedTest
    @MethodSource("loginRequests")
    void login_shouldThrowException_whenPasswordIsInvalid(LoginRequest request) {
        configureAndCreateLoginRequest(request, Optional.of(savedUser));

        when(passwordEncoder.matches(
                request.password(),
                savedUser.getPasswordHash()
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(
                mapper,
                sessionService,
                jwtService
        );
    }

    @ParameterizedTest
    @MethodSource("loginRequests")
    void login_shouldThrowException_whenUserBlocked(LoginRequest request) {
        User blockedUser = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .passwordHash("encoded-password")
                .role(Role.ROLE_CLIENT)
                .status(UserStatus.BLOCKED)
                .build();

        configureAndCreateLoginRequest(request, Optional.of(blockedUser));

        when(passwordEncoder.matches(
                request.password(),
                blockedUser.getPasswordHash()
        )).thenReturn(true);

        assertThrows(
                UserBlockedException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(
                mapper,
                sessionService,
                jwtService
        );
    }

    @Test
    void register_success() {
        when(repository.existsByEmail(request.email()))
                .thenReturn(false);
        when(repository.existsByUsername(request.username()))
                .thenReturn(false);
        when(mapper.toEntity(request))
                .thenReturn(user);
        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");
        when(repository.save(user))
                .thenReturn(savedUser);

        when(mapper.toPrincipal(savedUser))
                .thenReturn(userPrincipal);

        Session session = new Session(
                UUID.randomUUID(),
                savedUser.getId(),
                UUID.randomUUID(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 60_000
        );

        Tokens tokens = new Tokens(accessToken, refreshToken);

        configureCreateSessionWithTokens(
                savedUser,
                userPrincipal,
                session,
                tokens
        );

        Tokens result = authService.register(request);

        assertSame(tokens, result);

        assertAuthentication(userPrincipal);

        verify(sessionService).createSession(userPrincipal);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        when(repository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(repository).existsByEmail(request.email());
        verify(repository, never()).save(any());
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        when(repository.existsByEmail(request.email()))
                .thenReturn(false);
        when(repository.existsByUsername(request.username()))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(repository).existsByEmail(request.email());
        verify(repository).existsByUsername(request.username());
        verify(repository, never()).save(any());
    }

    @Test
    void refresh_success() {
        UUID oldJti = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID newJti = UUID.randomUUID();

        when(jwtService.validateRefresh(refreshToken))
                .thenReturn(true);
        when(jwtService.extractJTI(refreshToken))
                .thenReturn(oldJti);
        when(jwtService.extractSessionIdRefresh(refreshToken))
                .thenReturn(sessionId);
        when(sessionService.validate(sessionId, oldJti))
                .thenReturn(true);
        when(jwtService.extractUserIdRefresh(refreshToken))
                .thenReturn(id);
        when(repository.findById(id))
                .thenReturn(Optional.of(savedUser));
        when(mapper.toPrincipal(savedUser))
                .thenReturn(userPrincipal);
        when(sessionService.rotateJTI(sessionId))
                .thenReturn(newJti);

        Tokens tokens = new Tokens(accessToken, refreshToken);

        when(jwtService.generateTokenPair(
                userPrincipal,
                sessionId,
                newJti
        )).thenReturn(tokens);

        Tokens result = authService.refresh(refreshToken);

        assertSame(tokens, result);

        assertAuthentication(userPrincipal);

        verify(jwtService).validateRefresh(refreshToken);
        verify(sessionService).validate(sessionId, oldJti);
        verify(repository).findById(id);
        verify(sessionService).rotateJTI(sessionId);
        verify(jwtService).generateTokenPair(
                userPrincipal,
                sessionId,
                newJti
        );
    }

    @Test
    void refresh_shouldThrowException_whenRefreshTokenInvalid() {
        String refreshToken = "invalid-refresh-token";

        when(jwtService.validateRefresh(refreshToken))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.refresh(refreshToken)
        );

        verify(jwtService).validateRefresh(refreshToken);

        verifyNoInteractions(
                sessionService,
                repository,
                mapper
        );
    }

    @Test
    void refresh_shouldThrowException_whenSessionIsInvalid() {
        UUID oldJti = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(jwtService.validateRefresh(refreshToken))
                .thenReturn(true);

        when(jwtService.extractJTI(refreshToken))
                .thenReturn(oldJti);

        when(jwtService.extractSessionIdRefresh(refreshToken))
                .thenReturn(sessionId);

        when(sessionService.validate(sessionId, oldJti))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.refresh(refreshToken)
        );

        verify(jwtService).validateRefresh(refreshToken);
        verify(jwtService).extractJTI(refreshToken);
        verify(jwtService).extractSessionIdRefresh(refreshToken);
        verify(sessionService).validate(sessionId, oldJti);

        verifyNoInteractions(
                repository,
                mapper
        );

        verify(sessionService, never()).rotateJTI(any());
        verify(jwtService, never()).generateTokenPair(
                any(),
                any(),
                any()
        );
    }

    @Test
    void refresh_shouldThrowException_whenUserNotFound() {
        UUID oldJti = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(jwtService.validateRefresh(refreshToken))
                .thenReturn(true);

        when(jwtService.extractJTI(refreshToken))
                .thenReturn(oldJti);

        when(jwtService.extractSessionIdRefresh(refreshToken))
                .thenReturn(sessionId);

        when(sessionService.validate(sessionId, oldJti))
                .thenReturn(true);

        when(jwtService.extractUserIdRefresh(refreshToken))
                .thenReturn(id);

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.refresh(refreshToken)
        );

        verify(repository).findById(id);

        verify(mapper, never()).toPrincipal(any());
        verify(sessionService, never()).rotateJTI(any());
        verify(jwtService, never()).generateTokenPair(
                any(),
                any(),
                any()
        );
    }

    @Test
    void logout_success() {
        UUID oldJti = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(jwtService.extractJTI(refreshToken))
                .thenReturn(oldJti);

        when(jwtService.extractSessionIdRefresh(refreshToken))
                .thenReturn(sessionId);

        when(sessionService.validate(sessionId, oldJti))
                .thenReturn(true);

        when(jwtService.extractUserIdRefresh(refreshToken))
                .thenReturn(id);

        authService.logout(refreshToken);

        verify(jwtService).extractJTI(refreshToken);
        verify(jwtService).extractSessionIdRefresh(refreshToken);
        verify(sessionService).validate(sessionId, oldJti);
        verify(sessionService).deleteSession(sessionId, id);
    }

    @Test
    void logout_shouldThrowException_whenSessionIsInvalid() {
        UUID oldJti = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(jwtService.extractJTI(refreshToken))
                .thenReturn(oldJti);

        when(jwtService.extractSessionIdRefresh(refreshToken))
                .thenReturn(sessionId);

        when(sessionService.validate(sessionId, oldJti))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.logout(refreshToken)
        );

        verify(sessionService).validate(sessionId, oldJti);
        verify(sessionService, never()).deleteSession(any(), any());
    }

    private void configureAndCreateLoginRequest(LoginRequest request, Optional<User> userOptional) {
        if (request.login().contains("@")) {
            when(repository.findByEmail(request.login()))
                    .thenReturn(userOptional);
        } else {
            when(repository.findByUsername(request.login()))
                    .thenReturn(userOptional);
        }
    }

    private void configureCreateSessionWithTokens(
            User saved,
            UserPrincipal principal,
            Session session,
            Tokens expectedTokens
    ) {
        when(mapper.toPrincipal(saved))
                .thenReturn(principal);

        when(sessionService.createSession(principal))
                .thenReturn(session);

        when(jwtService.generateTokenPair(
                principal,
                session.getSessionId(),
                session.getJti()
        )).thenReturn(expectedTokens);
    }

    private void assertAuthentication(UserPrincipal expectedPrincipal) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(expectedPrincipal, authentication.getPrincipal());
        assertEquals(expectedPrincipal.getAuthorities(),
                authentication.getAuthorities());
    }
}
