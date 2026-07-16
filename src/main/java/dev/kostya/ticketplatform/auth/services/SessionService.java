package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.security.Session;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;

import java.util.UUID;

public interface SessionService {
    Session createSession(UserPrincipal userPrincipal);
    void updateJTI(UUID sessionId, UUID newJti);
    boolean validate(UUID sessionId, UUID jti);
    void save(Session session);
}
