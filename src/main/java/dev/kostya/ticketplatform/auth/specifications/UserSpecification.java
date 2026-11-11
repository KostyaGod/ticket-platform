package dev.kostya.ticketplatform.auth.specifications;

import dev.kostya.ticketplatform.auth.dto.request.UserFilter;
import dev.kostya.ticketplatform.auth.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static dev.kostya.ticketplatform.common.specifications.SpecificationSupport.*;

/**
 * Спецификация JPA для динамической фильтрации пользователей.
 * Собирает критерии поиска по имени пользователя, email, роли, статусу блокировки и общим метаданным.
 */
@UtilityClass
public class UserSpecification {
    /**
     * Формирует комплексный предикат фильтрации пользователей на основе DTO фильтра {@link UserFilter}.
     *
     * @param filter критерии фильтрации
     * @return JPA спецификация {@link Specification}
     */
    public static Specification<User> build(UserFilter filter) {
        if (filter == null) return null;

        return Specification.allOf(
                stringContains( "username", filter.username() ),
                stringContains( "email", filter.email() ),
                equalTo( "role", filter.role() ),
                equalTo( "userStatus", filter.userStatus() ),
                filterByMetadata( filter.metadataFilter() )
        );
    }
}
