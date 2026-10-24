package dev.kostya.ticketplatform.infrastructure.sync.dto;

import dev.kostya.ticketplatform.infrastructure.entity.Hall;

public record HallSyncDto(
        Long id,
        String name,
        Long venueId
) {
    public HallSyncDto(Hall hall) {
        this(
                hall.getId(),
                hall.getName(),
                hall.getVenueId()
        );
    }
}
