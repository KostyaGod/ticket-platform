package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceImplTest {
    @Mock
    private JwtProperties properties;
    @InjectMocks
    private SessionServiceImpl sessionService;

    private final UserPrincipal userPrincipal =
            new UserPrincipal(1L, Role.ROLE_USER);

    @Test
    void createSession_shouldCreateValidSession() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        long before = System.currentTimeMillis();

        Session session = sessionService.createSession(userPrincipal);

        long after = System.currentTimeMillis();

        assertNotNull(session);

        assertNotNull(session.getSessionId());
        assertEquals(1L, session.getUserId());
        assertNotNull(session.getJti());

        assertTrue(session.getCreatedAt() >= before);
        assertTrue(session.getCreatedAt() <= after);

        assertTrue(session.getExpiresAt() >= before + 60_000L);
        assertTrue(session.getExpiresAt() <= after + 60_000L);
    }

    @Test
    void createSession_shouldSaveSession() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        Session session = sessionService.createSession(userPrincipal);

        assertTrue(
                sessionService.validate(
                        session.getSessionId(),
                        session.getJti()
                )
        );
    }

    @Test
    void updateJTI_shouldReplaceSessionJTI() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        Session session = sessionService.createSession(userPrincipal);

        UUID oldJti = session.getJti();
        UUID newJti = UUID.randomUUID();

        sessionService.updateJTI(session.getSessionId(), newJti);

        assertNotEquals(oldJti, newJti);
        assertFalse(
                sessionService.validate(
                        session.getSessionId(),
                        oldJti
                )
        );

        assertTrue(
                sessionService.validate(
                        session.getSessionId(),
                        newJti
                )
        );
    }

    @Test
    void updateJTI_shouldDoNothing_whenSessionDoesNotExist() {
        UUID sessionId = UUID.randomUUID();
        UUID newJti = UUID.randomUUID();

        sessionService.updateJTI(sessionId, newJti);

        assertDoesNotThrow(
                () -> sessionService.updateJTI(sessionId, newJti)
        );
    }

    @Test
    void validate_shouldReturnTrue_whenSessionAndJTIAreValid() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        Session session = sessionService.createSession(userPrincipal);

        assertTrue(
                sessionService.validate(
                        session.getSessionId(),
                        session.getJti()
                )
        );
    }

    @Test
    void validate_shouldReturnFalse_whenSessionAndJTIIsInvalid() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        Session session = sessionService.createSession(userPrincipal);
        UUID wrongJti  = UUID.randomUUID();

        assertFalse(
                sessionService.validate(
                        session.getSessionId(),
                        wrongJti
                )
        );
    }

    @Test
    void validate_shouldReturnFalse_whenSessionDoesNotExist() {
        UUID sessionId = UUID.randomUUID();
        UUID jti = UUID.randomUUID();

        assertFalse(
                sessionService.validate(sessionId, jti)
        );
    }

    @Test
    void deleteSession_shouldRemoveSession() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        Session session = sessionService.createSession(userPrincipal);

        assertTrue(
                sessionService.validate(
                        session.getSessionId(),
                        session.getJti()
                )
        );

        sessionService.deleteSession(session.getSessionId());

        assertFalse(
                sessionService.validate(
                        session.getSessionId(),
                        session.getJti()
                )
        );
    }

    @Test
    void deleteSession_shouldDoNothing_whenSessionDoesNotExist() {
        UUID sessionId = UUID.randomUUID();

        assertDoesNotThrow(
                () -> sessionService.deleteSession(sessionId)
        );
    }

    @Test
    void save_shouldSaveSession() {
        UUID sessionId = UUID.randomUUID();
        UUID sessionJti = UUID.randomUUID();

        Session session = new Session(
                sessionId,
                userPrincipal.getId(),
                sessionJti,
                System.currentTimeMillis(),
                System.currentTimeMillis() + 60_000
        );

        assertFalse(
                sessionService.validate(sessionId, sessionJti)
        );

        sessionService.save(session);

        assertTrue(
                sessionService.validate(sessionId, sessionJti)
        );
    }
}
