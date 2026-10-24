package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadataFilter;

public record CityFilter(
        String name,
        EntityMetadataFilter metadataFilter
) {
    public CityFilter {
        name = (name == null) ? null : name.trim();
    }
}
