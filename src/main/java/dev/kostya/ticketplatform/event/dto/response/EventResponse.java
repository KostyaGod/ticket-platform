package dev.kostya.ticketplatform.event.dto.response;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        String category,
        Integer ageLimit,
        String organizer,
        LocalDateTime createdAt
) {
}
