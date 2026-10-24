package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.Range;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record SessionUpdateRequest(
        @Positive(message = "Event ID must be positive")
        Long eventId,

        EventSessionStatus status,

        Range<Instant> salesTimeRange,

        Range<Instant> sessionTimeRange
) {
}
