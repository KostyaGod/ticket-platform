package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.common.dto.base.Range;

import java.time.Instant;
import java.time.LocalTime;

public record SessionCalendarFilter(
        Range<Instant> date,
        Range<LocalTime> time
) { }
