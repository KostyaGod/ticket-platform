package dev.kostya.ticketplatform.auth.dto.base;

import dev.kostya.ticketplatform.auth.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.auth.enums.EntityStatus;

import java.time.Instant;

public record EntityMetadataFilter(
        EntityStatus status,
        Range<Instant> createdAtRange,
        DeletedViewFilter deletedFilter,
        Range<Instant> deletedAtRange
) {
}
