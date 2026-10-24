package dev.kostya.ticketplatform.infrastructure.dto.base;

import dev.kostya.ticketplatform.infrastructure.entity.SoftDeletableEntity;
import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;

import java.time.Instant;
import java.util.UUID;

public record EntityMetadata <T extends SoftDeletableEntity>(
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
