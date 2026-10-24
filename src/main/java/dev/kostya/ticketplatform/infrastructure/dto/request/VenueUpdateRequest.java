package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;

public record VenueUpdateRequest(
        String name,
        Long cityId,
        EntityStatus status
) {
    public VenueUpdateRequest {
        name = (name == null) ? null : name.trim();
    }
}
