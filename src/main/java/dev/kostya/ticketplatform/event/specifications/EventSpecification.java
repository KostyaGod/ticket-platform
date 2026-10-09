package dev.kostya.ticketplatform.event.specifications;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.entity.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventSpecification {
    public static Specification<Event> build(EventCatalogFilter filter) {
        return Specification
                .<Event>where(stringContains("title", filter.title()))
                .and(stringContains("description", filter.description()))
                .and(stringContains("category", filter.category()))
                .and(greaterThanOrEqualTo("ageLimit", filter.ageLimit()))
                .and(stringContains("organizer", filter.organizer()));
    }

    private static Specification<Event> cityEquals(Long cityId) {
        return (root, query, cb) -> {
            if (cityId == null) return null;

            query.distinct(true);

            return cb.equal(
                    root.join("event_sessions")
                            .join("hall")
                            .join("venue")
                            .join("city")
                            .get("id"),
                    cityId
            );
        };
    }
}
