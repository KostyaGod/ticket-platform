package dev.kostya.ticketplatform.event.specifications;

import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.entity.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventSpecification {

    public static Specification<Event> forFilter(EventFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                stringContains( "title", filter.title() ),
                stringContains( "description", filter.description() ),
                stringContains( "category", filter.category() ),
                greaterThanOrEqualTo( "ageLimit", filter.ageLimit() ),
                between(
                        "releaseDate",
                        filter.releaseDateRange().from(),
                        filter.releaseDateRange().to()
                ),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
