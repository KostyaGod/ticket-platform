package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;

import java.util.UUID;

public interface SessionService {
    Session createSession(UserPrincipal userPrincipal);
    void deleteSession(UUID sessionId);
    UUID rotateJTI(UUID sessionId);
    boolean validate(UUID sessionId, UUID jti);
    void save(Session session);
}
