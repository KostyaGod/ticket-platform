package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.event.dto.base.Range;
import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.enums.EntityStatus;

import java.time.Instant;
import java.time.LocalDate;

public record EventFilter(
        String title,
        String description,
        String category,
        Integer ageLimit,
        Range<LocalDate> releaseDateRange,
        EntityMetadataFilter metadataFilter
) { }
