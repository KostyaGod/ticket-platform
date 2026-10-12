package dev.kostya.ticketplatform.event.dto.base;

import java.time.Instant;

public record InstantRange(
        Instant from,
        Instant to
) {
}
