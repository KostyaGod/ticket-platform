package dev.kostya.ticketplatform.infrastructure.dto.base;

import dev.kostya.ticketplatform.infrastructure.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;

import java.time.Instant;

public record EntityMetadataFilter(
        EntityStatus status,
        Range<Instant> createdAtRange,
        DeletedViewFilter deletedFilter,
        Range<Instant> deletedAtRange
) {
}
