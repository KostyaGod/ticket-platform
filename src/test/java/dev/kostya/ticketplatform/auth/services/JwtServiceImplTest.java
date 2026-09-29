package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Tokens;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {
    private JwtProperties properties;

    private JwtServiceImpl jwtService;

    private final long id = 1L;
    private final Role role = Role.ROLE_CLIENT;
    private final UserPrincipal principal = new UserPrincipal(
            id,
            role
    );
    private final UUID sessionId = UUID.randomUUID();
    private final UUID jti = UUID.randomUUID();

    private static final String ACCESS_SECRET = Base64.getEncoder()
            .encodeToString(
                    "access-secret-key-access-secret-key-123456".getBytes()
            );

    private static final String REFRESH_SECRET = Base64.getEncoder()
            .encodeToString(
                    "refresh-secret-key-refresh-secret-key-123456".getBytes()
            );

    @BeforeEach
    void setUp() {
        properties = mock(JwtProperties.class);

        when(properties.getAccessSecret())
                .thenReturn(ACCESS_SECRET);

        when(properties.getRefreshSecret())
                .thenReturn(REFRESH_SECRET);

        jwtService = new JwtServiceImpl(properties);
        jwtService.init();
    }

    @Test
    void generateTokenPair_success() {
        when(properties.getAccessExpiration())
                .thenReturn(15 * 60 * 1000L);

        when(properties.getRefreshExpiration())
                .thenReturn(7 * 24 * 60 * 60 * 1000L);

        Tokens tokens = jwtService.generateTokenPair(
                principal,
                sessionId,
                jti
        );

        assertNotNull(tokens);
        assertNotNull(tokens.access());
        assertNotNull(tokens.refresh());

        assertEquals(
                3,
                tokens.access().split("\\.").length
        );
        assertEquals(
                3,
                tokens.refresh().split("\\.").length
        );
    }

    @Test
    void generateAccessToken_success() {
        when(properties.getAccessExpiration())
                .thenReturn(15 * 60 * 1000L);

        String token = jwtService.generateAccessToken(
                principal,
                sessionId
        );

        assertNotNull(token);
        assertEquals(
                id,
                jwtService.extractUserIdAccess(token)
        );
        assertEquals(
                role,
                jwtService.extractRole(token)
        );
        assertEquals(
                sessionId,
                jwtService.extractSessionIdAccess(token)
        );
    }

    @Test
    void generateRefreshToken_success() {
        when(properties.getRefreshExpiration())
                .thenReturn(7 * 24 * 60 * 60 * 1000L);

        String token = jwtService.generateRefreshToken(
                principal,
                sessionId,
                jti
        );

        assertNotNull(token);

        assertEquals(
                id,
                jwtService.extractUserIdRefresh(token)
        );
        assertEquals(
                sessionId,
                jwtService.extractSessionIdRefresh(token)
        );
        assertEquals(
                jti,
                jwtService.extractJTI(token)
        );
    }

    @Test
    void validateAccess_success() {
        when(properties.getAccessExpiration())
                .thenReturn(15 * 60 * 1000L);

        String token = jwtService.generateAccessToken(
                principal,
                sessionId
        );

        assertTrue(jwtService.validateAccess(token));
    }

    @Test
    void validateRefresh_success() {
        when(properties.getRefreshExpiration())
                .thenReturn(7 * 24 * 60 * 60 * 1000L);

        String token = jwtService.generateRefreshToken(
                principal,
                sessionId,
                jti
        );

        assertTrue(jwtService.validateRefresh(token));
    }

    @Test
    void validateAccess_shouldReturnFalse_forRefreshToken() {
        when(properties.getRefreshExpiration())
                .thenReturn(7 * 24 * 60 * 60 * 1000L);

        String refreshToken = jwtService.generateRefreshToken(
                principal,
                sessionId,
                jti
        );

        assertFalse(jwtService.validateAccess(refreshToken));
    }

    @Test
    void validateRefresh_shouldReturnFalse_forAccessToken() {
        when(properties.getAccessExpiration())
                .thenReturn(15 * 60 * 1000L);

        String accessToken = jwtService.generateAccessToken(
                principal,
                sessionId
        );

        assertFalse(jwtService.validateRefresh(accessToken));
    }

    @Test
    void validateAccess_shouldReturnFalse_forInvalidToken() {
        assertFalse(
                jwtService.validateAccess("invalid.token.value")
        );
    }

    @Test
    void validateRefresh_shouldReturnFalse_forInvalidToken() {
        assertFalse(
                jwtService.validateRefresh("invalid.token.value")
        );
    }

    @Test
    void validateAccess_shouldReturnFalse_whenTokenWasModified() {
        when(properties.getAccessExpiration())
                .thenReturn(15 * 60 * 1000L);

        String token = jwtService.generateAccessToken(
                principal,
                sessionId
        );

        String modifiedToken = token + "modified";

        assertFalse(
                jwtService.validateAccess(modifiedToken)
        );
    }

    @Test
    void validateRefresh_shouldReturnFalse_whenTokenWasModified() {
        when(properties.getRefreshExpiration())
                .thenReturn(7 * 24 * 60 * 60 * 1000L);

        String token = jwtService.generateRefreshToken(
                principal,
                sessionId,
                jti
        );

        String modifiedToken = token + "modified";

        assertFalse(
                jwtService.validateRefresh(modifiedToken)
        );
    }

    @Test
    void accessToken_shouldContainRoleButNotJti() {
        when(properties.getAccessExpiration())
                .thenReturn(15 * 60 * 1000L);

        String token = jwtService.generateAccessToken(
                principal,
                sessionId
        );

        var claims = Jwts.parser()
                .verifyWith(getKey(ACCESS_SECRET))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(
               role.name(),
                claims.get("role", String.class)
        );

        assertEquals(
                sessionId.toString(),
                claims.get("session", String.class)
        );

        assertNull(claims.get("jti"));
    }

    @Test
    void refreshToken_shouldContainJtiButNotRole() {
        when(properties.getRefreshExpiration())
                .thenReturn(7 * 24 * 60 * 60 * 1000L);

        String token = jwtService.generateRefreshToken(
                principal,
                sessionId,
                jti
        );

        var claims = Jwts.parser()
                .verifyWith(getKey(REFRESH_SECRET))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(
                jti.toString(),
                claims.get("jti", String.class)
        );

        assertEquals(
                sessionId.toString(),
                claims.get("session", String.class)
        );

        assertNull(claims.get("role"));
    }

    private javax.crypto.SecretKey getKey(String secret) {
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(secret)
        );
    }
}