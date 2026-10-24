package dev.kostya.ticketplatform.auth.dto.base;

import dev.kostya.ticketplatform.auth.entity.SoftDeletableEntity;
import dev.kostya.ticketplatform.auth.enums.EntityStatus;

import java.time.Instant;
import java.util.UUID;

public record EntityMetadata<T extends SoftDeletableEntity>(
        Instant createdAt,
        Instant deletedAt,
        UUID deleteOperation,
        EntityStatus status
) {
    public EntityMetadata(T entity) {
        this(
                entity.getCreatedAt(),
                entity.getDeletedAt(),
                entity.getDeletionOperationId(),
                entity.getStatus()
        );
    }
}
