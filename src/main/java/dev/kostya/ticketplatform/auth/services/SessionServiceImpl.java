package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.repositories.SessionRepository;
import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final JwtProperties properties;
    private final SessionRepository repository;

    @Override
    public Session createSession(UserPrincipal userPrincipal) {
        UUID sessionId = UUID.randomUUID();
        UUID sessionJti = UUID.randomUUID();

        Session session = new Session(
                sessionId,
                userPrincipal.getId(),
                sessionJti,
                System.currentTimeMillis(),
                System.currentTimeMillis() + properties.getRefreshExpiration()
        );

        repository.save(session);

        return session;
    }

    @Override
    public UUID rotateJTI(UUID sessionId) {
        UUID newJti = UUID.randomUUID();
        repository.updateJti(sessionId, newJti);

        return newJti;
    }

    @Override
    public void deleteSession(UUID sessionId, Long userId) {
        repository.delete(sessionId, userId);
    }

    @Override
    public void deleteAllSessions(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    @Override
    public boolean validate(UUID sessionId, UUID jti) {
        return repository.findById(sessionId)
                .map(session -> Objects.equals(session.getJti(), jti))
                .orElse(false);
    }

    @Override
    public void save(Session session) {
        repository.save(session);
    }
}
