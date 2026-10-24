package dev.kostya.ticketplatform.infrastructure.dto.base;

import dev.kostya.ticketplatform.infrastructure.entity.IdentifiableAndNamed;

public record EntityReference <T extends IdentifiableAndNamed>(
        Long id,
        String name
) {
    public EntityReference(T entity) {
        this(entity.getId(), entity.getName());
    }
}
