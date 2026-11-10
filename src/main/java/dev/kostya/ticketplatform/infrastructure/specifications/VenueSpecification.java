package dev.kostya.ticketplatform.infrastructure.specifications;

import dev.kostya.ticketplatform.infrastructure.dto.request.VenueFilter;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VenueSpecification {

    public static Specification<Venue> filterByVenueFilter(VenueFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                stringContains("name", filter.name()),
                equalTo("city.id", filter.cityId()),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
