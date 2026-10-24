package dev.kostya.ticketplatform.infrastructure.dto.request;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CityUpdateRequest(
        @Size(max = 255, message = "Name must not exceed 255 characters")
        @Pattern(regexp = ".*\\S.*", message = "Name must not be empty")
        String name,

        @Size(max = 64, message = "Timezone must not exceed 64 characters")
        @Pattern(regexp = ".*\\S.*", message = "Timezone must not be empty")
        String timezone,

        EntityStatus status
) {
        public CityUpdateRequest {
                name = (name == null) ? null : name.trim();
                timezone = (timezone == null) ? null : timezone.trim();
        }
}
