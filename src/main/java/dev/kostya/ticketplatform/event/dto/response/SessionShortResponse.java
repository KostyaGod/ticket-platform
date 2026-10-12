package dev.kostya.ticketplatform.event.dto.response;

import java.time.Instant;

public record SessionShortResponse(
        Long id,
        Long venueId,
        String venueName,
        Instant startAt
) {
}
