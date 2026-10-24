package dev.kostya.ticketplatform.event.sync.dto;

public record HallSnapshotDto(
        Long id,
        String name,
        Long venueId
) {
}
