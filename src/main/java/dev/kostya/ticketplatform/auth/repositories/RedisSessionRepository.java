package dev.kostya.ticketplatform.auth.repositories;

import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepository implements SessionRepository {
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties properties;

    private static final String SESSION_KEY_PREFIX = "session";
    private static final String USER_KEY_PREFIX = "user";

    private static final String USER_ID_FIELD = "userId";
    private static final String JTI_FIELD = "jti";
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String EXPIRES_AT_FIELD = "expiresAt";

    @Override
    public void save(Session session) {
        String sessionKey = sessionKey(session.getSessionId());

        Map<String, String> data = Map.of(
                USER_ID_FIELD, session.getUserId().toString(),
                JTI_FIELD, session.getJti().toString(),
                CREATED_AT_FIELD, String.valueOf(session.getCreatedAt()),
                EXPIRES_AT_FIELD, String.valueOf(session.getExpiresAt())
        );

        redisTemplate.opsForHash().putAll(sessionKey, data);
        redisTemplate.expire(
                sessionKey,
                Duration.ofMillis(
                        session.getExpiresAt() - System.currentTimeMillis()
                )
        );
        redisTemplate.opsForSet().add(
                userKey(session.getUserId()),
                session.getSessionId().toString()
        );
    }

    @Override
    public void updateJti(UUID sessionId, UUID jti) {
        redisTemplate.opsForHash().put(
                sessionKey(sessionId),
                JTI_FIELD,
                jti.toString()
        );
    }

    @Override
    public Optional<Session> findById(UUID sessionId) {
        String key = sessionKey(sessionId);

        Map<Object, Object> data =
                redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            return Optional.empty();
        }

        Session session = new Session(
                sessionId,
                Long.parseLong((String) data.get(USER_ID_FIELD)),
                UUID.fromString((String) data.get(JTI_FIELD)),
                Long.parseLong((String) data.get(CREATED_AT_FIELD)),
                Long.parseLong((String) data.get(EXPIRES_AT_FIELD))
        );

        return Optional.of(session);
    }

    @Override
    public void delete(UUID sessionId, Long userId) {
        redisTemplate.delete(
                sessionKey(sessionId)
        );

        redisTemplate.opsForSet().remove(
                userKey(userId),
                sessionId.toString()
        );
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        String userKey = userKey(userId);

        Set<String> sessionIds = redisTemplate.opsForSet()
                .members(userKey);

        if (sessionIds != null && !sessionIds.isEmpty()) {
            redisTemplate.delete(
                    sessionIds.stream()
                            .map(this::sessionKey)
                            .collect(Collectors.toList()
                            )
            );

            redisTemplate.delete(userKey);
        }
    }

    @Override
    public Set<UUID> findSessionIdsByUserId(Long userId) {
        Set<String> sessionIds = redisTemplate.opsForSet()
                .members(userKey(userId));

        if (sessionIds == null) return Set.of();

        return sessionIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    private String sessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    private String sessionKey(UUID sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    private String userKey(String userId) {
        return USER_KEY_PREFIX + userId;
    }

    private String userKey(Long userId) {
        return USER_KEY_PREFIX + userId;
    }
}
