package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.enums.EntityStatus;

import java.time.LocalDate;

public record EventUpdateRequest(
        String title,
        String description,
        String catalog,
        Integer ageLimit,
        String organizer,
        LocalDate releaseDate,
        EntityStatus status
) {
}
