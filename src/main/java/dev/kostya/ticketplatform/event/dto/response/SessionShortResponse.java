package dev.kostya.ticketplatform.event.dto.response;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.entity.VenueSnapshot;

import java.time.Instant;

public record SessionShortResponse(
        Long id,
        EntityReference<VenueSnapshot> venue,
        Instant startAt
) {
}
