package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EventCatalogRepositoryImpl implements EventCatalogRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<EventCatalogResponse> findCatalogWithFilters(
            Long cityId,
            EventCatalogFilter filter,
            Pageable pageable
    ) {
        StringBuilder sb = new StringBuilder("""
        SELECT
            e.id AS id,
            e.title AS title,
            e.category AS category,
            e.age_limit AS age_limit,
            s.start_at AS next_session_start_at
        FROM events e
        JOIN LATERAL (
            SELECT es.start_at
            FROM event_sessions AS es
            WHERE es.event_id = e.id
              AND es.city_id = :cityId
              AND es.status = 'SCHEDULED'
              AND es.start_at > now()
              AND es.deleted_at IS NULL
            ORDER BY es.start_at
            LIMIT 1
        ) s ON TRUE
        WHERE e.status = 'ACTIVE'
          AND e.deleted_at IS NULL
        """);

        appendFilters(sb, filter);

        if (pageable.getSort().isSorted()) {
            sb.append(" ORDER BY ").append(
                    pageable.getSort().stream()
                            .map(order -> {
                                String sqlProperty = switch (order.getProperty()) {
                                    case "ageLimit" -> "e.age_limit";
                                    case "title" -> "e.title";
                                    case "category" -> "e.category";
                                    case "nextSessionStartAt" -> "s.start_at";
                                    default -> "e.id";
                                };

                                return sqlProperty + " " + order.getDirection().name();
                            })
                            .collect(Collectors.joining(", "))
                    );
        }

        Query query = entityManager.createNativeQuery(sb.toString(), Tuple.class);
        query.setParameter("cityId", cityId);
        setParametersFromFilter(query, filter);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Tuple> results = query.getResultList();
        List<EventCatalogResponse> content = results.stream()
                .map(tuple -> new EventCatalogResponse(
                        tuple.get("id", Long.class),
                        tuple.get("title", String.class),
                        tuple.get("category", String.class),
                        tuple.get("age_limit", Integer.class),
                        tuple.get("next_session_start_at", Instant.class)
                )).toList();

        long total = countForCatalog(cityId, filter);

        return new PageImpl<>(content, pageable, total);
    }

    private long countForCatalog(
            Long cityId,
            EventCatalogFilter filter
    ) {
        StringBuilder sb = new StringBuilder("""
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
        """);

        appendFilters(sb, filter);
        Query query = entityManager.createNativeQuery(sb.toString(), Tuple.class);
        query.setParameter("cityId", cityId);
        setParametersFromFilter(query, filter);

        Number totalCount = (Number) query.getSingleResult();

        return totalCount.longValue();
    }

    private void appendFilters(StringBuilder sb, EventCatalogFilter filter) {
        if (filter != null) {
            if (filter.title() != null) {
                sb.append(" AND e.title = :title ");
            }
            if (filter.category() != null) {
                sb.append(" AND e.category = :category ");
            }
            if (filter.description() != null) {
                sb.append(" AND e.description = :description ");
            }
            if (filter.ageLimit() != null) {
                sb.append(" AND e.ageLimit = :ageLimit ");
            }
        }
    }

    private void setParametersFromFilter(Query query, EventCatalogFilter filter) {
        if (filter != null) {
            if (filter.title() != null) {
                query.setParameter("title", filter.title());
            }
            if (filter.category() != null) {
                query.setParameter("category", filter.category());
            }
            if (filter.description() != null) {
                query.setParameter("description", filter.description());
            }
            if (filter.ageLimit() != null) {
                query.setParameter("ageLimit", filter.ageLimit());
            }
        }
    }
}
