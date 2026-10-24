package dev.kostya.ticketplatform.event.specifications;

import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.entity.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.between;
import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.equalTo;
import static dev.kostya.ticketplatform.event.specifications.SpecificationSupport.filterByDeleted;
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
        EntityMetadataFilter metadataFilter = filter.metadataFilter();

        Specification<Event> specification = Specification
                .<Event>where(stringContains("title", filter.title()))
                .and(stringContains("description", filter.description()))
                .and(stringContains("category", filter.category()))
                .and(greaterThanOrEqualTo("ageLimit", filter.ageLimit()))
                .and(between(
                        "releaseDate",
                        filter.releaseDateRange().from(),
                        filter.releaseDateRange().to())
                );

        if (metadataFilter != null) {
            specification = specification
                    .and(equalTo("status", metadataFilter.status()))
                    .and(between(
                            "createdAt",
                            metadataFilter.createdAtRange().from(),
                            metadataFilter.createdAtRange().to())
                    )
                    .and(filterByDeleted(metadataFilter.deletedFilter()));

            if (metadataFilter.deletedFilter().equals(DeletedViewFilter.DELETED_ONLY)) {
                specification = specification
                        .and(between(
                                "deletedAt",
                                metadataFilter.deletedAtRange().from(),
                                metadataFilter.deletedAtRange().to())
                        );
            }
        }

        return specification;
    }
}
