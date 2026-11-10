package dev.kostya.ticketplatform.infrastructure.specifications;

import dev.kostya.ticketplatform.infrastructure.dto.request.SeatFilter;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.enums.DeletedViewFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SeatSpecification {

    public static Specification<Seat> filterByRowNumberAndSeatNumber(
            Integer rowNumber,
            Integer seatNumber
    ) {
        return Specification.allOf(
                equalTo("rowNumber", rowNumber),
                equalTo("seatNumber", seatNumber),
                filterByDeleted( null )
        );
    }

    public static Specification<Seat> filterBySeatFilter(SeatFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                equalTo("hall.id", filter.hallId()),
                equalTo("rowNumber", filter.rowNumber()),
                equalTo("seatNumber", filter.seatNumber()),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
