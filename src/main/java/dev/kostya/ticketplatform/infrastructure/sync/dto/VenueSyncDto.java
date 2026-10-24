package dev.kostya.ticketplatform.infrastructure.sync.dto;

import dev.kostya.ticketplatform.infrastructure.entity.Venue;

public record VenueSyncDto(
        Long id,
        String name,
        Long cityId
) {
    public VenueSyncDto(Venue venue) {
        this(
                venue.getId(),
                venue.getName(),
                venue.getCityId()
        );
    }
}
