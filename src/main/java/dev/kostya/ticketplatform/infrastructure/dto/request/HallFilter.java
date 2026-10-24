package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadataFilter;

public record HallFilter(
        String name,
        Long venueId,
        EntityMetadataFilter metadataFilter
) {
}
