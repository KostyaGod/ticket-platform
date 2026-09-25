package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.repositories.SessionRepository;
import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceImplTest {
    @Mock
    private JwtProperties properties;
    @Mock
    private SessionRepository repository;
    @InjectMocks
    private SessionServiceImpl sessionService;

    private final long id = 1L;
    private final UUID sessionId = UUID.randomUUID();
    private final UUID jti = UUID.randomUUID();

    private final UserPrincipal userPrincipal =
            new UserPrincipal(id, Role.ROLE_USER);

    private final Session session = new Session(
            sessionId,
            userPrincipal.getId(),
            jti,
            System.currentTimeMillis(),
            System.currentTimeMillis() + 60_000
    );

    @Test
    void createSession_shouldCreateValidSession() {
        when(properties.getRefreshExpiration())
                .thenReturn(60_000L);

        long before = System.currentTimeMillis();

        Session session = sessionService.createSession(userPrincipal);

        long after = System.currentTimeMillis();

        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertNotNull(session.getJti());
        assertEquals(id, session.getUserId());

        assertTrue(session.getCreatedAt() >= before);
        assertTrue(session.getCreatedAt() <= after);

        assertTrue(session.getExpiresAt() >= before + 60_000L);
        assertTrue(session.getExpiresAt() <= after + 60_000L);

        verify(repository).save(session);
    }

    @Test
    void rotateJTI_shouldReplaceSessionJTI() {
        UUID result = sessionService.rotateJTI(sessionId);

        assertNotNull(result);

        verify(repository).updateJti(
                sessionId,
                result
        );
    }

    @Test
    void deleteSession_shouldDeleteSession() {
        sessionService.deleteSession(sessionId, id);

        verify(repository).delete(
                sessionId,
                id
        );
    }

    @Test
    void validate_shouldReturnTrue_whenSessionExistsAndJtiMatches() {
        when(repository.findById(sessionId))
                .thenReturn(Optional.of(session));

        boolean result = sessionService.validate(
                sessionId,
                jti
        );

        assertTrue(result);

        verify(repository).findById(sessionId);
    }

    @Test
    void validate_shouldReturnFalse_whenJtiDoesNotMatch() {
        UUID wrongJti = UUID.randomUUID();

        when(repository.findById(sessionId))
                .thenReturn(Optional.of(session));

        boolean result = sessionService.validate(
                sessionId,
                wrongJti
        );

        assertFalse(result);

        verify(repository).findById(sessionId);
    }

    @Test
    void validate_shouldReturnFalse_whenSessionNotFound() {
        when(repository.findById(sessionId))
                .thenReturn(Optional.empty());

        boolean result = sessionService.validate(
                sessionId,
                jti
        );

        assertFalse(result);

        verify(repository).findById(sessionId);
    }

    @Test
    void save_shouldSaveSession() {
        sessionService.save(session);

        verify(repository).save(session);
    }

}
