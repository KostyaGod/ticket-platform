package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.Range;
import java.time.Instant;
import java.time.LocalDateTime;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;

public record SessionFilter(
        Long eventId,
        Long hallId,
        Long venueId,
        Long organizationId,
        EventSessionStatus eventStatus,
        Range<Instant> salesTimeFilter,
        Range<Instant> sessionTimeFilter,
        Range<LocalDateTime> dateTimeFilter
) { }
