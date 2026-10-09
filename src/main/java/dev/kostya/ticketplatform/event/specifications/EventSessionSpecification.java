package dev.kostya.ticketplatform.event.specifications;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.entity.EventSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventSessionSpecification {
    public static Specification<EventSession> build(EventCatalogFilter filter) {
        return Specification
                .<EventSession>where(equalTo("cityId", filter.cityId()))
                .and(equalTo("venueId", filter.venueId()))
//                .and(equalTo("hallId", filter.hallId()))
//                .and(equalTo("organizationId", security...))
                .and(equalTo("status", filter.status()))
                .and(greaterThanOrEqualTo("startAt", Instant.now()))
                .and(stringContains("title", filter.title()))
                .and(stringContains("description", filter.description()))
                .and(stringContains("category", filter.category()))
                .and(greaterThanOrEqualTo("ageLimit", filter.ageLimit()))
                .and(stringContains("organizer", filter.organizer()));
    }
}
