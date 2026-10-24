package dev.kostya.ticketplatform.event.dto.response;

import dev.kostya.ticketplatform.event.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.event.entity.Event;

import java.time.LocalDate;

public record EventDetailsResponse(
        Long id,
        String title,
        String description,
        String category,
        Integer ageLimit,
        String organizer,
        LocalDate releaseDate,
        EntityMetadata<Event> metadata
) {
}
