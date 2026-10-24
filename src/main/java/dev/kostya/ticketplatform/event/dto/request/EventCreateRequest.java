package dev.kostya.ticketplatform.event.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EventCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        @Pattern(regexp = ".*\\S.*", message = "Description must not be empty")
        String description,

        @Min(value = 0, message = "Duration must be non-negative")
        Integer duration,

        @Size(max = 100, message = "Category must not exceed 100 characters")
        @Pattern(regexp = ".*\\S.*", message = "Category must not be empty")
        String category,

        @Min(value = 0, message = "Age limit must be non-negative")
        @Max(value = 150, message = "Age limit must not exceed 150")
        Integer ageLimit,

        @Size(max = 255, message = "Organizer must not exceed 255 characters")
        @Pattern(regexp = ".*\\S.*", message = "Organizer must not be empty")
        String organizer,

        LocalDate releaseDate
) { }
