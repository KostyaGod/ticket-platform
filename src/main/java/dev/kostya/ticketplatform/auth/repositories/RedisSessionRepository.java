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

/**
 * Репозиторий для управления пользовательскими сессиями в хранилище Redis.
 * <p>
 * Архитектура хранения сессий использует две взаимосвязанные структуры данных:
 * <ul>
 *   <li><b>Redis Hash (ключ "session:{sessionId}"):</b> Хранит детальные атрибуты конкретной сессии:
 *       ID пользователя ({@code userId}), текущий JWT ID ({@code jti}), время создания ({@code createdAt})
 *       и время истечения ({@code expiresAt}). Для данного ключа устанавливается TTL, равный сроку жизни Refresh токена.</li>
 *   <li><b>Redis Set (ключ "user:{userId}"):</b> Содержит множество всех активных {@code sessionId} для конкретного пользователя.
 *       Позволяет быстро находить все сессии пользователя (например, для личного кабинета или сброса всех сессий при смене пароля).</li>
 * </ul>
 * <p>
 * Для исключения проблемы N+1 сетевых запросов при получении списка сессий используется <b>Redis Pipelining</b>,
 * а также реализован механизм <i>ленивой сборки мусора (Lazy Garbage Collection)</i> для очистки ключей из Set,
 * если соответствующий Hash сессии уже удалился по TTL.
 */
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

    /**
     * Сохраняет новую сессию в Redis.
     * <p>
     * 1. Сохраняет параметры сессии в Hash с ключом "session:{sessionId}".<br>
     * 2. Выставляет TTL ключа Hash на оставшееся время жизни сессии.<br>
     * 3. Добавляет {@code sessionId} в Set "user:{userId}" для отслеживания сессий пользователя.
     *
     * @param session доменный объект сессии
     */
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

    /**
     * Атомарно обновляет JWT ID (JTI) сессии при ротации Refresh токена.
     * Защищает от Replay-атак: если токен попытаются использовать повторно с устаревшим JTI,
     * валидация будет отклонена.
     *
     * @param sessionId идентификатор сессии
     * @param jti       новый сгенерированный JTI
     */
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

    /**
     * Возвращает список всех активных сессий пользователя с отметкой текущей сессии.
     * <p>
     * Для максимальной производительности и исключения проблемы N+1 сетевых запросов:
     * 1. Считывается список {@code sessionId} из Redis Set "user:{userId}".
     * 2. Выполняется <b>Redis Pipeline</b> ({@code executePipelined}): все команды {@code HGETALL} для всех сессий
     *    пакетируются и отправляются в Redis за один сетевой запрос.
     * 3. Если по какому-либо ID хеш сессии вернул пустой результат (сессия истекла по TTL в Redis),
     *    такие протухшие ID удаляются из Set пользователя (пакетная ленивая очистка).
     *
     * @param userId           идентификатор пользователя
     * @param currentSessionId идентификатор текущей активной сессии из контекста запроса
     * @return список DTO сессий с флагом isCurrent
     */
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

    /**
     * Возвращает множество активных {@code sessionId} для пользователя с одновременной
     * валидацией существования ключей в Redis через pipeline и удалением протухших сессий из Set.
     *
     * @param userId идентификатор пользователя
     * @return множество валидных активных {@code sessionId}
     */
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
