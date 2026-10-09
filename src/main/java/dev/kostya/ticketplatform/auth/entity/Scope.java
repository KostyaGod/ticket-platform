package dev.kostya.ticketplatform.auth.entity;

import dev.kostya.ticketplatform.auth.enums.EntityStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class Scope extends SoftDeletableEntity {
    private Long id;

    private User user;

    private UUID organizationId;

    private UUID venueId;

    private EntityStatus status;
}
