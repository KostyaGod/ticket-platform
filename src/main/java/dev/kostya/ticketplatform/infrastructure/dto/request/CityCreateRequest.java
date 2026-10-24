package dev.kostya.ticketplatform.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CityCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Timezone is required")
        @Size(max = 64, message = "Timezone must not exceed 64 characters")
        String timezone
) {
}
