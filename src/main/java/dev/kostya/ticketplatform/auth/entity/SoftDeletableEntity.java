package dev.kostya.ticketplatform.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Setter @Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class SoftDeletableEntity extends BaseAuditEntity {

    @Column(name = "deleted_at")
    protected Instant deletedAt;

    @Column(name = "deletion_operation_id")
    protected UUID deletionOperationId;

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void markDeleted(UUID operationId) {
        this.deletedAt = Instant.now();
        this.deletionOperationId = operationId;
    }

    public UUID markDeleted() {
        UUID deletionOperationId = UUID.randomUUID();
        markDeleted(deletionOperationId);

        return deletionOperationId;
    }

    public UUID restore() {
        UUID deletionOperationId = this.deletionOperationId;

        this.deletedAt = null;
        this.deletionOperationId = null;

        return deletionOperationId;
    }
}
