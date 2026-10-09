package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter @Getter
@MappedSuperclass
public abstract class SoftDeletableEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deletion_operation_id")
    private UUID deletionOperationId;
}
