package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.common.dto.base.Range;

import java.time.LocalDate;
import java.time.LocalTime;

public record SessionCalendarFilter(
        Range<LocalDate> date,
        Range<LocalTime> time
) { }
