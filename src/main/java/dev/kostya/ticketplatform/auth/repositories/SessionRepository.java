package dev.kostya.ticketplatform.auth.repositories;

import dev.kostya.ticketplatform.auth.security.Session;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findById(UUID sessionId);
    void updateJti(UUID sessionId, UUID jti);
    void delete(UUID sessionId, Long userId);
    void deleteAllByUserId(Long userId);
    Set<UUID> findSessionIdsByUserId(Long userId);
}
