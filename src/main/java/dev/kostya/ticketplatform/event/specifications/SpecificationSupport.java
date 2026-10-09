package dev.kostya.ticketplatform.event.specifications;

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
            return null;
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
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get(field), value);
    }

    public static <T, V extends Comparable<? super V>> Specification<T> greaterThanOrEqualTo(
            String field,
            V value
    ) {
        if (value == null) {
            return null;
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
            return null;
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
            return null;
        } else if (from == null) {
            return lessThanOrEqualTo(field, to);
        } else if (to == null) {
            return greaterThanOrEqualTo(field, from);
        }

        return (root, query, cb) ->
                cb.between(root.get(field), from, to);
    }
}
