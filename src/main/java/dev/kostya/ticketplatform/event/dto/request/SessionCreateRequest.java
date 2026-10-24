package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.Range;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record SessionCreateRequest(
        @NotNull(message = "Event ID is required")
        @Positive(message = "Event ID must be positive")
        Long eventId,

        @NotNull(message = "Hall ID is required")
        @Positive(message = "Hall ID must be positive")
        Long hallId,

        @NotNull(message = "Organization ID is required")
        @Positive(message = "Organization ID must be positive")
        Long organizationId,

        @NotNull(message = "Status is required")
        EventSessionStatus eventStatus,

        @NotNull(message = "Sales time range is required")
        Range<Instant> salesTimeRange,

        @NotNull(message = "Session time range is required")
        Range<Instant> sessionTimeRange
) {
}
