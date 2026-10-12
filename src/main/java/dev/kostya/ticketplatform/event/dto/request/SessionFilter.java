package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.DateTimeRange;
import dev.kostya.ticketplatform.event.dto.base.InstantRange;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;

public record SessionFilter(
        Long eventId,
        Long hallId,
        Long venueId,
        Long organizationId,
        EventSessionStatus status,
        InstantRange salesTimeFilter,
        InstantRange sessionTimeFilter,
        DateTimeRange dateTimeFilter
) { }
