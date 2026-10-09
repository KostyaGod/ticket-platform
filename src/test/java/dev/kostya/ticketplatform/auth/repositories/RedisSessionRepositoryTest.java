package dev.kostya.ticketplatform.auth.repositories;

import dev.kostya.ticketplatform.auth.exception.SessionNotFoundException;
import dev.kostya.ticketplatform.auth.security.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisSessionRepositoryTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private RedisSessionRepository repository;

    private static final String SESSION_KEY_PREFIX = "session";
    private static final String USER_KEY_PREFIX = "user";

    private final Session session = new Session(
            UUID.randomUUID(),
            1L,
            UUID.randomUUID(),
            System.currentTimeMillis(),
            System.currentTimeMillis() + 60_000
    );

    @Test
    void save_success() {
        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        repository.save(session);

        verify(hashOperations).putAll(
                eq(SESSION_KEY_PREFIX + session.getSessionId()),
                eq(Map.of(
                        "userId", session.getUserId().toString(),
                        "jti", session.getJti().toString(),
                        "createdAt", String.valueOf(session.getCreatedAt()),
                        "expiresAt", String.valueOf(session.getExpiresAt())
                ))
        );

        verify(redisTemplate).expire(
                eq(SESSION_KEY_PREFIX + session.getSessionId()),
                argThat(duration ->
                        duration.toMillis() > 0 &&
                                duration.toMillis() <= 60_000
                )
        );

        verify(setOperations).add(
                USER_KEY_PREFIX + session.getUserId(),
                session.getSessionId().toString()
        );
    }

    @Test
    void updateJti_success() {
        UUID newJti = UUID.randomUUID();

        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        repository.updateJti(session.getSessionId(), newJti);

        verify(hashOperations).put(
                SESSION_KEY_PREFIX + session.getSessionId().toString(),
                "jti",
                newJti.toString()
        );

        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    void findById_success() {
        Map<Object, Object> data = Map.of(
                "userId", session.getUserId().toString(),
                "jti", session.getJti().toString(),
                "createdAt", String.valueOf(session.getCreatedAt()),
                "expiresAt", String.valueOf(session.getExpiresAt())
        );

        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        when(hashOperations.entries(SESSION_KEY_PREFIX + session.getSessionId()))
                .thenReturn(data);

        Optional<Session> result = repository.findById(session.getSessionId());

        assertTrue(result.isPresent());

        Session found = result.get();

        assertEquals(session.getSessionId(), found.getSessionId());
        assertEquals(session.getUserId(), found.getUserId());
        assertEquals(session.getJti(), found.getJti());
        assertEquals(session.getCreatedAt(), found.getCreatedAt());
        assertEquals(session.getExpiresAt(), found.getExpiresAt());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenEmptyData() {
        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        when(hashOperations.entries(
                SESSION_KEY_PREFIX + session.getSessionId()
        )).thenReturn(Map.of());

        Optional<Session> result = repository.findById(session.getSessionId());

        assertTrue(result.isEmpty());
    }

    @Test
    void delete_success() {
        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        when(setOperations.isMember(
                SESSION_KEY_PREFIX + session.getSessionId(),
                USER_KEY_PREFIX + session.getUserId()
        )).thenReturn(Boolean.TRUE);

        repository.delete(session.getSessionId(), session.getUserId());

        verify(redisTemplate).delete(
                SESSION_KEY_PREFIX + session.getSessionId()
        );

        verify(setOperations).remove(
                USER_KEY_PREFIX + session.getUserId(),
                session.getSessionId().toString()
        );
    }

    @Test
    void delete_shouldThrowException_whenSessionIdDoesNotBelongToUserId() {
        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        when(setOperations.isMember(
                SESSION_KEY_PREFIX + session.getSessionId(),
                USER_KEY_PREFIX + session.getUserId()
        )).thenReturn(Boolean.FALSE);

        assertThrows(
                SessionNotFoundException.class,
                () -> repository.delete(
                        session.getSessionId(),
                        session.getUserId())
        );

        verify(redisTemplate, never())
                .delete(anyString());
        verify(setOperations, never())
                .remove(any());
    }

    @Test
    void existsBySessionIdAndUserId_success() {
        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        when(setOperations.isMember(
                SESSION_KEY_PREFIX + session.getSessionId(),
                USER_KEY_PREFIX + session.getUserId()
        )).thenReturn(Boolean.TRUE);

        boolean result = repository.existsBySessionIdAndUserId(
                session.getSessionId(),
                session.getUserId()
        );

        assertTrue(result);
    }

    @Test
    void findSessionIdsByUserId_success() {
        UUID secondSessionId = UUID.randomUUID();

        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        Set<String> set = new HashSet<>();
        set.add(session.getSessionId().toString());
        set.add(secondSessionId.toString());

        when(setOperations.members(USER_KEY_PREFIX + session.getUserId()))
                .thenReturn(set);

        when(redisTemplate.hasKey(any()))
                .thenReturn(Boolean.TRUE);

        Set<UUID> result =
                repository.findSessionIdsByUserId(session.getUserId());

        assertEquals(
                Set.of(session.getSessionId(), secondSessionId),
                result
        );
    }

    @Test
    void findSessionIdsByUserId_shouldReturnEmptySet_whenNoSessions() {
        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        when(setOperations.members(USER_KEY_PREFIX + session.getUserId()))
                .thenReturn(null);

        Set<UUID> result =
                repository.findSessionIdsByUserId(session.getUserId());

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllByUserId_success() {
        UUID secondSessionId = UUID.randomUUID();

        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        Set<String> set = new HashSet<>();
        set.add(session.getSessionId().toString());
        set.add(secondSessionId.toString());

        when(setOperations.members(USER_KEY_PREFIX + session.getUserId()))
                .thenReturn(set);

        when(redisTemplate.hasKey(any()))
                .thenReturn(Boolean.TRUE);

        repository.deleteAllByUserId(session.getUserId());

        ArgumentCaptor<List<String>> keysCaptor = ArgumentCaptor.forClass(List.class);

        verify(redisTemplate).delete(keysCaptor.capture());

        assertThat(keysCaptor.getValue())
                .containsExactlyInAnyOrder(
                        SESSION_KEY_PREFIX + session.getSessionId(),
                        SESSION_KEY_PREFIX + secondSessionId
                );

        verify(redisTemplate).delete(
                USER_KEY_PREFIX + session.getUserId()
        );
    }

    @Test
    void deleteAllByUserId_shouldDoNothing_whenNoSessions() {
        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        when(setOperations.members(USER_KEY_PREFIX + session.getUserId()))
                .thenReturn(null);

        repository.deleteAllByUserId(session.getUserId());

        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void deleteAllByUserId_shouldDoNothing_whenSessionSetIsEmpty() {
        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);

        when(setOperations.members(USER_KEY_PREFIX + session.getUserId()))
                .thenReturn(Set.of());

        repository.deleteAllByUserId(session.getUserId());

        verify(redisTemplate, never()).delete(anyString());
    }
}
