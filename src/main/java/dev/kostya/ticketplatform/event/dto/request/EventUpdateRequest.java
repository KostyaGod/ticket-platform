package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.enums.EntityStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EventUpdateRequest(
        @Size(max = 255, message = "Title must not exceed 255 characters")
        @Pattern(regexp = ".*\\S.*", message = "Title must not be empty")
        String title,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        @Pattern(regexp = ".*\\S.*", message = "Description must not be empty")
        String description,

        @Size(max = 100, message = "Category must not exceed 100 characters")
        @Pattern(regexp = ".*\\S.*", message = "Catalog must not be empty")
        String catalog,

        @Min(value = 0, message = "Age limit must be non-negative")
        @Max(value = 150, message = "Age limit must not exceed 150")
        Integer ageLimit,

        @Size(max = 255, message = "Organizer must not exceed 255 characters")
        @Pattern(regexp = ".*\\S.*", message = "Organizer must not be empty")
        String organizer,

        LocalDate releaseDate,

        EntityStatus status
) {
        public EventUpdateRequest {
                title = (title == null) ? null : title.trim();
                description = (description == null) ? null : description.trim();
                catalog = (catalog == null) ? null : catalog.trim();
                organizer = (organizer == null) ? null : organizer.trim();
        }
}
