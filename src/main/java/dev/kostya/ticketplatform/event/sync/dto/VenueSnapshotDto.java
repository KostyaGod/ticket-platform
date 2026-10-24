package dev.kostya.ticketplatform.event.sync.dto;

public record VenueSnapshotDto(
        Long id,
        String name,
        Long cityId
) {
}
