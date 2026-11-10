package dev.kostya.ticketplatform.infrastructure.specifications;

import dev.kostya.ticketplatform.infrastructure.dto.request.CityFilter;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.filterByMetadata;
import static dev.kostya.ticketplatform.infrastructure.specifications.SpecificationSupport.stringContains;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CitySpecification {

    public static Specification<City> filterByCityFilter(CityFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                stringContains("name", filter.name()),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
