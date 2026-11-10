package dev.kostya.ticketplatform.event.specifications;

import dev.kostya.ticketplatform.event.dto.base.Range;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.entity.EventSession;
import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventSessionSpecification {

    public static Specification<EventSession> filterByEventAndDeleted(
            Long eventId,
            DeletedViewFilter deletedFilter
    ) {
        return Specification.allOf(
                equalTo( "event.id", eventId ),
                filterByDeleted( deletedFilter )
        );
    }

    public static Specification<EventSession> filterByEventAndDateRange(
            Long eventId,
            Range<Instant> dateFilter
    ) {
        return Specification.allOf(
                equalTo( "event.id", eventId ),
                between(
                        "startAt",
                        dateFilter.from(),
                        dateFilter.to()
                ),
                filterByDeleted( null )
        );
    }

    public static Specification<EventSession> filterBySessionFilter(SessionFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                equalTo( "event.id", filter.eventId() ),
                equalTo( "city.id", filter.cityId() ),
                equalTo( "hall.id", filter.hallId() ),
                equalTo( "venue.id", filter.eventId() ),
                equalTo( "organizationId", filter.organizationId() ),
                equalTo( "eventStatus", filter.eventStatus() ),
                between(
                        "salesStartAt",
                        filter.salesTimeFilter().from(),
                        filter.salesTimeFilter().to()
                ),
                between(
                        "startAt",
                        filter.sessionTimeFilter().from(),
                        filter.sessionTimeFilter().to()
                ),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
