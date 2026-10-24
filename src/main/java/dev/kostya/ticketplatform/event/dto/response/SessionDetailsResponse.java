package dev.kostya.ticketplatform.event.dto.response;

import dev.kostya.ticketplatform.event.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.entity.*;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;

import java.time.Instant;

public record SessionDetailsResponse(
        Long id,
        EntityReference<Event> event,
        EntityReference<HallSnapshot> hall,
        EntityReference<VenueSnapshot> venue,
        EntityReference<CitySnapshot> city,
        Long organizationId,
        EventSessionStatus eventStatus,
        Instant salesStartAt,
        Instant startAt,
        Instant endAt,
        EntityMetadata<EventSession> metadata
) {
}
