package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.auth.dto.base.EntityMetadataFilter;

public record SeatFilter(
        Long hallId,
        Integer rowNumber,
        Integer seatNumber,
        EntityMetadataFilter metadataFilter
) {
}
