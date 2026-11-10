package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.event.dto.base.Range;
import java.time.Instant;

import dev.kostya.ticketplatform.event.enums.EventSessionStatus;

public record SessionFilter(
        Long eventId,
        Long cityId,
        Long hallId,
        Long venueId,
        Long organizationId,
        EventSessionStatus eventStatus,
        Range<Instant> salesTimeFilter, // ??
        Range<Instant> sessionTimeFilter, // ??
        EntityMetadataFilter metadataFilter
) { }
