package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;

public record HallUpdateRequest(
        String name,
        Long venueId,
        EntityStatus status
) {
    public HallUpdateRequest {
        name = (name == null) ? null : name.trim();
    }
}
