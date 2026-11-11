package dev.kostya.ticketplatform.common.dto.base;

import dev.kostya.ticketplatform.common.entity.IdentifiableAndNamed;

public record EntityReference <T extends IdentifiableAndNamed>(
        Long id,
        String name
) {
    public EntityReference(T entity) {
        this(entity.getId(), entity.getName());
    }

    public static <T extends IdentifiableAndNamed> EntityReference<T> of(
            IdentifiableAndNamed projection
    ) {
        return projection != null
                ? new EntityReference<>( projection.getId(), projection.getName() )
                : null;
    }
}
