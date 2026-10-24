package dev.kostya.ticketplatform.infrastructure.dto.request;

import jakarta.validation.constraints.Positive;

public record SeatMapCreateRequest(
        @Positive(message = "Row count must be positive")
        int rowCount,

        @Positive(message = "Seat count must be positive")
        int seatCount
) {
}
