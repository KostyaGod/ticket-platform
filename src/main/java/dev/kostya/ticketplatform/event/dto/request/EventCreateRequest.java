package dev.kostya.ticketplatform.event.dto.request;

import java.time.LocalDate;

public record EventCreateRequest(
        String title,
        String description,
        String category,
        Integer ageLimit,
        String organizer,
        LocalDate releaseDate
) { }
