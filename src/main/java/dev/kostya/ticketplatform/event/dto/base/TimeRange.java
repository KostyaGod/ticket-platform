package dev.kostya.ticketplatform.event.dto.base;

import java.time.LocalTime;

public record TimeRange(
        LocalTime from,
        LocalTime to
) {
}
