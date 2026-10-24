package dev.kostya.ticketplatform.infrastructure.entity;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseAuditEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    protected EntityStatus status;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false
    )
    protected Instant createdAt;
}
