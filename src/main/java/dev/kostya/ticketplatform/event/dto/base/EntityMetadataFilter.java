package dev.kostya.ticketplatform.event.dto.base;

import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.enums.EntityStatus;

import java.time.Instant;

public record EntityMetadataFilter(
        EntityStatus status,
        Range<Instant> createdAtRange,
        DeletedViewFilter deletedFilter,
        Range<Instant> deletedAtRange
) {
}
