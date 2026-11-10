package dev.kostya.ticketplatform.event.dto.response;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.entity.VenueSnapshot;

import java.time.Instant;

public record SessionShortResponse(
        Long id,
        EntityReference<VenueSnapshot> venue,
        Instant startAt
) {
    public SessionShortResponse(Long id, VenueSnapshot venue, Instant startAt) {
        this(
                id,
                new EntityReference<>(venue),
                startAt
        );
    }

    public SessionShortResponse(Long id, Long venueId, String venueName, Instant startAt) {
        this(
                id,
                new EntityReference<>(venueId, venueName),
                startAt
        );
    }
}
