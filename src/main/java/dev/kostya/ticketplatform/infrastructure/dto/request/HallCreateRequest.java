package dev.kostya.ticketplatform.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HallCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @NotNull(message = "Venue ID is required")
        @Positive(message = "Venue ID must be positive")
        Long venueId,

        SeatMapCreateRequest seatMap
) { }
