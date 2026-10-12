package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.InstantRange;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;

public record SessionUpdateRequest(
        Long eventId,
        EventSessionStatus status,
        InstantRange salesTimeRange,
        InstantRange sessionTimeRange
) { }
