package dev.kostya.ticketplatform.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CityCreateRequest(
        @NotBlank(message = "Name is required")
        String name
) {
}
