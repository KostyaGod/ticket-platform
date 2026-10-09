package dev.kostya.ticketplatform.auth.specifications;

import dev.kostya.ticketplatform.auth.dto.request.UserFilter;
import dev.kostya.ticketplatform.auth.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.auth.specifications.SpecificationSupport.*;

@UtilityClass
public class UserSpecification {
    public static Specification<User> build(UserFilter filter) {
        if (filter == null) {
            return null;
        }

        return Specification
                .<User>where(stringContains("username", filter.username()))
                .and(stringContains("email", filter.email()))
                .and(equalTo("role", filter.role()))
                .and(equalTo("status", filter.status()))
                .and(between("createdAt", filter.createdAfter(), filter.createdBefore()));
    }
}
