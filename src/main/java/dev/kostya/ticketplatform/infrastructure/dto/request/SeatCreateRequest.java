package dev.kostya.ticketplatform.infrastructure.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SeatCreateRequest(
        @NotNull(message = "Hall ID is required")
        @Positive(message = "Hall ID must be positive")
        Long hallId,

        @NotNull(message = "Row number is required")
        @PositiveOrZero(message = "Row number must be positive or zero")
        Integer rowNumber,

        @NotNull(message = "Seat number is required")
        @Positive(message = "Seat number must be positive")
        Integer seatNumber
) {
}
