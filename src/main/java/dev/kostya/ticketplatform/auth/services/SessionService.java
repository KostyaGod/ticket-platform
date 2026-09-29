package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.response.SessionResponse;
import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    Session createSession(UserPrincipal userPrincipal);
    void deleteSession(UUID sessionId, Long userId);
    void deleteAllSessions(Long userId);
    UUID rotateJTI(UUID sessionId);
    boolean validate(UUID sessionId, UUID jti);
    void save(Session session);

    List<SessionResponse> getSessions(Long userId, UUID currentSessionId);
}
