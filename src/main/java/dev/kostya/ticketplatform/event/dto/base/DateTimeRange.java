package dev.kostya.ticketplatform.event.dto.base;

import java.time.LocalDateTime;

public record DateTimeRange(
        LocalDateTime from,
        LocalDateTime to
) {
}
