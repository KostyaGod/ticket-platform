package dev.kostya.ticketplatform.common.specifications;

import dev.kostya.ticketplatform.common.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.common.dto.base.Range;
import dev.kostya.ticketplatform.common.entity.SoftDeletableEntity;
import dev.kostya.ticketplatform.common.enums.DeletedViewFilter;
import jakarta.persistence.criteria.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Вспомогательный класс для динамического построения JPA Criteria {@link Specification}.
 * <p>
 * Предоставляет набор переиспользуемых фабричных методов для типовых предикатов:
 * <ul>
 *   <li>Регистронезависимый поиск подстроки (LIKE %value%).</li>
 *   <li>Сравнение по равенству с поддержкой вложенных свойств через точку (например, "hall.venue.id").</li>
 *   <li>Фильтрация диапазонов (>=, <=, BETWEEN) с поддержкой открытых интервалов (nullable границ).</li>
 *   <li>Унифицированная фильтрация сущностей с поддержкой Soft Delete (активные, удаленные, все).</li>
 *   <li>Комплексная фильтрация по аудиторным метаданным (статус, дата создания, дата удаления).</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpecificationSupport {

    /**
     * Создает предикат для поиска подстроки без учета регистра символов:
     * {@code LOWER(field) LIKE '%value%'}.
     * Если передана пустая строка или {@code null}, ограничение не накладывается.
     *
     * @param field имя строкового поля сущности
     * @param value искомая подстрока
     * @param <T>   тип корневой сущности
     * @return спецификация JPA
     */
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

    /**
     * Создает предикат точного совпадения поля со значением.
     * <p>
     * <b>Поддержка вложенных путей:</b> если имя поля содержит точки (например, {@code "hall.id"}),
     * метод последовательно проходит по графу связей JPA Entity ({@link Path#get(String)}).
     *
     * @param field имя поля или вложенный путь (например, "user.profile.email")
     * @param value значение для сравнения (при null ограничение не накладывается)
     * @param <T>   тип сущности
     * @return спецификация JPA
     */
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

    /**
     * Создает предикат сравнения "больше либо равно" ({@code >=}).
     *
     * @param field имя сравниваемого поля
     * @param value нижняя граница
     * @param <T>   тип сущности
     * @param <V>   тип значения поля (должен реализовывать Comparable)
     * @return спецификация JPA
     */
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

    /**
     * Создает предикат сравнения "меньше либо равно" ({@code <=}).
     *
     * @param field имя сравниваемого поля
     * @param value верхняя граница
     * @param <T>   тип сущности
     * @param <V>   тип значения поля (должен реализовывать Comparable)
     * @return спецификация JPA
     */
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

    /**
     * Создает предикат для диапазона значений (BETWEEN) с поддержкой открытых интервалов.
     * Если задана только нижняя граница — преобразуется в {@code >=}.
     * Если задана только верхняя граница — преобразуется в {@code <=}.
     * Если обе границы {@code null} — ограничение отсутствует.
     *
     * @param field имя поля
     * @param from  начало диапазона (включительно)
     * @param to    конец диапазона (включительно)
     * @param <T>   тип сущности
     * @param <V>   тип значения поля
     * @return спецификация JPA
     */
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

    /**
     * Создает предикат диапазона на основе объекта {@link Range}.
     *
     * @param field имя поля
     * @param range объект диапазона с границами from и to
     * @param <T>   тип сущности
     * @param <V>   тип значения поля
     * @return спецификация JPA
     */
    public static <T, V extends Comparable<? super V>> Specification<T> between(
            String field,
            Range<V> range
    ) {
        if (range == null) {
            return Specification.unrestricted();
        }
        return between(field, range.from(), range.to());
    }

    /**
     * Создает предикат фильтрации сущностей с поддержкой мягкого удаления (Soft Delete):
     * <ul>
     *   <li>{@link DeletedViewFilter#ACTIVE_ONLY} (или {@code null}): только неудаленные записи ({@code deletedAt IS NULL}).</li>
     *   <li>{@link DeletedViewFilter#DELETED_ONLY}: только помеченные как удаленные ({@code deletedAt IS NOT NULL}).</li>
     *   <li>{@link DeletedViewFilter#ALL}: все записи без фильтрации по факту удаления.</li>
     * </ul>
     *
     * @param filter режим отображения удаленных записей
     * @param <T>    тип сущности, реализующей {@link SoftDeletableEntity}
     * @return спецификация JPA
     */
    public static <T extends SoftDeletableEntity> Specification<T> filterByDeleted(
            DeletedViewFilter filter
    ) {
        if (filter == null) {
            return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        } else if (filter.equals(DeletedViewFilter.ALL)) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> switch (filter) {
            case ACTIVE_ONLY -> cb.isNull(root.get("deletedAt"));
            case DELETED_ONLY -> cb.isNotNull(root.get("deletedAt"));
            default -> null;
        };
    }

    /**
     * Создает комплексный предикат фильтрации по общим метаданным сущности:
     * статус, диапазон даты создания, статус soft delete и опциональный диапазон даты удаления.
     *
     * @param filter DTO фильтра метаданных
     * @param <T>    тип сущности, поддерживающей soft delete
     * @return спецификация JPA
     */
    public static <T extends SoftDeletableEntity> Specification<T> filterByMetadata(
            EntityMetadataFilter filter
    ) {
        if (filter == null) {
            return Specification.unrestricted();
        }

        Specification<T> specification = Specification.allOf(
                equalTo("status", filter.status()),
                between("createdAt", filter.createdAtRange()),
                filterByDeleted(filter.deletedFilter())
        );

        if (filter.deletedFilter() != null && filter.deletedFilter().equals(DeletedViewFilter.DELETED_ONLY)) {
            specification = specification
                    .and(between("deletedAt", filter.deletedAtRange()));
        }

        return specification;
    }
}
