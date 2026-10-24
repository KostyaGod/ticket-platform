package dev.kostya.ticketplatform.auth.specifications;

import dev.kostya.ticketplatform.auth.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.auth.dto.request.UserFilter;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.DeletedViewFilter;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.auth.specifications.SpecificationSupport.*;

@UtilityClass
public class UserSpecification {
    public static Specification<User> build(UserFilter filter) {
        if (filter == null) {
            return null;
        }

        EntityMetadataFilter metadataFilter = filter.metadataFilter();

        Specification<User> specification = Specification
                .<User>where(stringContains("username", filter.username()))
                .and(stringContains("email", filter.email()))
                .and(equalTo("role", filter.role()))
                .and(equalTo("userStatus", filter.userStatus()));

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
