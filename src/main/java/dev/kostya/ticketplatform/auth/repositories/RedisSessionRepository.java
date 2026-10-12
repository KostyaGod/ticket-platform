package dev.kostya.ticketplatform.auth.repositories;

import dev.kostya.ticketplatform.auth.dto.response.SessionResponse;
import dev.kostya.ticketplatform.auth.exception.SessionNotFoundException;
import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
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
        if (!existsBySessionIdAndUserId(sessionId, userId))
            throw new SessionNotFoundException();

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

        if (sessionIds == null || sessionIds.isEmpty()) {
            redisTemplate.delete(userKey);
            return;
        }

        List<String> keys = sessionIds.stream()
                .map(this::sessionKey)
                .collect(Collectors.toList());

        keys.add(userKey);

        redisTemplate.delete(keys);
    }

    @Override
    public Set<UUID> findSessionIdsByUserId(Long userId) {
        Set<String> sessionIds = getAndCleanUpSessionsSet(userId);

        return sessionIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    @Override
    public List<SessionResponse> findAllByUserId(
            Long userId,
            UUID currentSessionId
    ) {
        String userKey = userKey(userId);

        Set<String> sessionIds = redisTemplate.opsForSet()
                .members(userKey);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        List<String> sessionIdList = new ArrayList<>(sessionIds);

        List<Object> results = redisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    byte[] keySerializer;
                    for (String id : sessionIdList) {
                        keySerializer = redisTemplate.getStringSerializer()
                                .serialize(sessionKey(id));

                        connection.hashCommands().hGetAll(keySerializer);
                    }

                    return null;
                }
        );

        List<String> expiredSessionIds = new ArrayList<>();
        List<SessionResponse> responses = new ArrayList<>();

        for (int i = 0; i < sessionIdList.size(); i++) {
            String id = sessionIdList.get(i);

            @SuppressWarnings("unchecked")
            Map<Object, Object> data = (Map<Object, Object>) results.get(i);

            if (data == null || data.isEmpty()) {
                expiredSessionIds.add(id);
                continue;
            }

            UUID sessionId = UUID.fromString(id);
            responses.add(new SessionResponse(
                    sessionId,
                    Long.parseLong((String) data.get(CREATED_AT_FIELD)),
                    Long.parseLong((String) data.get(EXPIRES_AT_FIELD)),
                    Objects.equals(currentSessionId, sessionId)
            ));
        }

        if (!expiredSessionIds.isEmpty()) {
            redisTemplate.opsForSet().remove(
                    userKey,
                    expiredSessionIds.toArray()
            );
        }

        return responses;
    }

    @Override
    public boolean existsBySessionIdAndUserId(UUID sessionId, Long userId) {
        String savedUserId = (String) redisTemplate.opsForHash().get(
                sessionKey(sessionId),
                USER_ID_FIELD
        );

        return Objects.equals(savedUserId, userId.toString());
    }

    private Set<String> getAndCleanUpSessionsSet(Long userId) {
        String userKey = userKey(userId);

        Set<String> sessionIds = redisTemplate.opsForSet()
                .members(userKey);
        log.info("SessionIds Before getAndCleanUpSessionsSet: {}", sessionIds);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return Set.of();
        }

        List<String> sessionIdList = new ArrayList<>(sessionIds);

        List<Object> results = redisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    byte[] key;
                    for (String id : sessionIdList) {
                        key = redisTemplate.getStringSerializer()
                                .serialize(sessionKey(id));

                        connection.keyCommands().exists(key);
                    }
                    return null;
                }
        );

        log.info("Result: {}", results);

        List<String> expiredSessionIds = new ArrayList<>();
        Set<String> activeSessionIds = new HashSet<>();

        for (int i  = 0; i < sessionIdList.size(); i++) {
            Boolean exists = (Boolean) results.get(i);
            if (Boolean.FALSE.equals(exists)) {
                expiredSessionIds.add(sessionIdList.get(i));
            } else {
                activeSessionIds.add(sessionIdList.get(i));
            }
        }

        if (!expiredSessionIds.isEmpty()) {
            redisTemplate.opsForSet().remove(
                    userKey,
                    expiredSessionIds.toArray()
            );
        }

        log.info("SessionIds After getAndCleanUpSessionsSet: {}", sessionIds);
        return activeSessionIds;
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
