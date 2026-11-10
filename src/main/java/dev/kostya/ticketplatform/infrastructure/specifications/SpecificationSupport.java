package dev.kostya.ticketplatform.infrastructure.specifications;

import dev.kostya.ticketplatform.infrastructure.entity.SoftDeletableEntity;
import dev.kostya.ticketplatform.infrastructure.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadataFilter;
import jakarta.persistence.criteria.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpecificationSupport {

    public static <T> Specification<T> stringContains(
            String field,
            String value
    ) {
        if (value == null || value.isBlank()) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get(field)),
                        "%" + value.toLowerCase() + "%"
                );
    }

    public static <T> Specification<T> equalTo(
            String field,
            Object value
    ) {
        if (value == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> {
            Path<?> path = root;
            for (String part : field.split("\\.")) {
                path = path.get(part);
            }
            return cb.equal(path, value);
        };
    }

    public static <T, V extends Comparable<? super V>> Specification<T> greaterThanOrEqualTo(
            String field,
            V value
    ) {
        if (value == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get(field),
                        value
                );
    }

    public static <T, V extends Comparable<? super V>> Specification<T> lessThanOrEqualTo(
            String field,
            V value
    ) {
        if (value == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get(field),
                        value
                );
    }

    public static <T, V extends Comparable<? super V>> Specification<T> between(
            String field,
            V from,
            V to
    ) {
        if (from == null && to == null) {
            return Specification.unrestricted();
        } else if (from == null) {
            return lessThanOrEqualTo(field, to);
        } else if (to == null) {
            return greaterThanOrEqualTo(field, from);
        }

        return (root, query, cb) ->
                cb.between(root.get(field), from, to);
    }

    public static <T extends SoftDeletableEntity> Specification<T> filterByDeleted(
            DeletedViewFilter filter
    ) {
        if (filter == null)
            return (root, query, cb) -> cb.isNull( root.get("deletedAt") );
        else if (filter.equals( DeletedViewFilter.ALL ))
            return Specification.unrestricted();

        return (root, query, cb) -> switch (filter) {
            case ACTIVE_ONLY -> cb.isNull( root.get("deletedAt") );
            case DELETED_ONLY -> cb.isNotNull( root.get("deletedAt") );
            default -> null;
        };
    }

    public static <T extends SoftDeletableEntity> Specification<T> filterByMetadata(
            EntityMetadataFilter filter
    ) {
        if (filter == null) return Specification.unrestricted();

        Specification<T> specification = Specification.allOf(
                equalTo("status", filter.status()),
                between(
                        "createdAt",
                        filter.createdAtRange().from(),
                        filter.createdAtRange().to()
                ),
                filterByDeleted(filter.deletedFilter())
        );

        if (filter.deletedFilter().equals(DeletedViewFilter.DELETED_ONLY)) {
            specification = specification
                    .and(between(
                            "deletedAt",
                            filter.deletedAtRange().from(),
                            filter.deletedAtRange().to())
                    );
        }

        return specification;
    }
}
