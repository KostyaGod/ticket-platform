package dev.kostya.ticketplatform.event.dto.base;

import java.time.LocalDate;

public record DateRange(
        LocalDate from,
        LocalDate to
) {
}
