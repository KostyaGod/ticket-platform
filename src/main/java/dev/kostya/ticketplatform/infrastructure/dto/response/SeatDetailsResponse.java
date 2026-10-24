package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;

public record SeatDetailsResponse(
        Long id,
        Long hallId,
        Integer rowNumber,
        Integer seatNumber,
        EntityMetadata<Seat> metadata
) {
}
