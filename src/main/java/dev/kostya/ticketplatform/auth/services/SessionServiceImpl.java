package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();
    private final JwtProperties properties;

    @Override
    public Session createSession(UserPrincipal userPrincipal) {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(
                sessionId,
                userPrincipal.getId(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + properties.getRefreshExpiration()
        );

        sessions.put(sessionId, session);

        return session;
    }

    @Override
    public void updateJTI(UUID sessionId, UUID newJti) {
        sessions.computeIfPresent(sessionId, (oldJti, session) -> {
            session.setJti(newJti);
            return session;
        });
    }

    @Override
    public boolean validate(UUID sessionId, UUID jti) {
        Session session = sessions.get(sessionId);

        return session != null && Objects.equals(session.getJti(), jti);
    }

    @Override
    public void save(Session session) {
        sessions.put(session.getSessionId(), session);
    }
}
