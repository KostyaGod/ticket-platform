package dev.kostya.ticketplatform.event.dto.base;

import dev.kostya.ticketplatform.event.entity.IdentifiableAndNamed;

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
        if (projection == null) return null;
        return new EntityReference<T>( projection.getId(), projection.getName() );
    }
}
