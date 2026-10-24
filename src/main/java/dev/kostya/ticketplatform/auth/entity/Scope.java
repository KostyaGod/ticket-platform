package dev.kostya.ticketplatform.auth.entity;

import dev.kostya.ticketplatform.auth.enums.EntityStatus;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class Scope {
    private Long id;

    private User user;

    private UUID organizationId;

    private UUID venueId;

//    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
//    private LocalDateTime createdAt;
}
