package dev.kostya.ticketplatform.common.dto.base;

import dev.kostya.ticketplatform.common.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.common.enums.EntityStatus;

import java.time.Instant;

public record EntityMetadataFilter(
        EntityStatus status,
        Range<Instant> createdAtRange,
        DeletedViewFilter deletedFilter,
        Range<Instant> deletedAtRange
) { }
