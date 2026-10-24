package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.auth.dto.base.EntityMetadataFilter;

public record VenueFilter(
        String name,
        Long cityId,
        EntityMetadataFilter metadataFilter
) {
}
