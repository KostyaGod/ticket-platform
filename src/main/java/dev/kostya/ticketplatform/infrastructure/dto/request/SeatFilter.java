package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadataFilter;

public record SeatFilter(
        Long hallId,
        Integer rowNumber,
        Integer seatNumber,
        EntityMetadataFilter metadataFilter
) {
}
