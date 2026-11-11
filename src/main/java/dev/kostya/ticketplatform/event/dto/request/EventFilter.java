package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.common.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.common.dto.base.Range;

import java.time.Instant;
import java.time.LocalDate;

public record EventFilter(
        String name,
        String description,
        String category,
        Integer ageLimit,
        Range<LocalDate> releaseDateRange,
        EntityMetadataFilter metadataFilter
) { }
