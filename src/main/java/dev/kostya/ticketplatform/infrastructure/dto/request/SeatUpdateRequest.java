package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;

public record SeatUpdateRequest(
        Long hallId,
        Integer rowNumber,
        Integer seatNumber,
        EntityStatus status
) {
}
