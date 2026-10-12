package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository
        extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    @Query(value = """
        SELECT
            e.id AS id,
            e.title AS title,
            e.category AS category,
            e.age_limit AS ageLimit,
            s.start_at AS nextSessionStartAt
        FROM events e
        JOIN LATERAL (
            SELECT es.start_at
            FROM event_sessions AS es
            WHERE es.event_id = e.id
              AND es.city_id = :cityId
              AND es.status = 'SCHEDULED'
              AND es.start_at > now()
            ORDER BY es.start_at
            LIMIT 1
        ) s ON TRUE
        WHERE e.status = 'ACTIVE'
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM events AS e
        WHERE e.status = 'ACTIVE'
          AND EXISTS(
              SELECT 1
              FROM event_sessions AS es
              WHERE es.event_id = e.id
                AND es.city_id = :cityId
                AND es.status = 'SCHEDULED'
                AND es.start_at > now()
          )
        """,
        nativeQuery = true
    )
    Page<Event> findCatalog(
            @Param("cityId") Long cityId,
            Pageable pageable);
}
