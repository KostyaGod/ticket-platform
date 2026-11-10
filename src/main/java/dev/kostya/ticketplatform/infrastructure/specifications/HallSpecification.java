package dev.kostya.ticketplatform.infrastructure.specifications;

import dev.kostya.ticketplatform.infrastructure.dto.request.HallFilter;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.filterByMetadata;
import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.stringContains;
import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.equalTo;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HallSpecification {

    public static Specification<Hall> filterByHallFilter(HallFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                stringContains("name", filter.name()),
                equalTo("venue.id", filter.venueId()),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
