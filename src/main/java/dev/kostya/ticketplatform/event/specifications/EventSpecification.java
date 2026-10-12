package dev.kostya.ticketplatform.event.specifications;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.entity.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventSpecification {
    public static Specification<Event> forCatalog(EventCatalogFilter filter) {
        return Specification
                .<Event>where(stringContains("title", filter.title()))
                .and(stringContains("description", filter.description()))
                .and(stringContains("category", filter.category()))
                .and(greaterThanOrEqualTo("ageLimit", filter.ageLimit()));
    }

    public static Specification<Event> forFilter(EventFilter filter) {
        return Specification
                .<Event>where(stringContains("title", filter.title()))
                .and(stringContains("description", filter.description()))
                .and(stringContains("category", filter.category()))
                .and(greaterThanOrEqualTo("ageLimit", filter.ageLimit()))
                .and(between("releaseDate", filter.releaseDateAfter(), filter.releaseDateBefore()))
                .and(equalTo("status", filter.status()))
                .and(between("createAt", filter.createdAfter(), filter.createdBefore()))
                .and(filterByDeleted(filter.deletedFilter()));
    }
}
