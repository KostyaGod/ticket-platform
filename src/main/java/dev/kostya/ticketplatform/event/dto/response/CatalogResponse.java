package dev.kostya.ticketplatform.event.dto.response;

import java.time.Instant;

public record CatalogResponse(
        Long eventId,
        String title,
        String description,
        String category,
        Integer ageLimit,
        Instant nextSession
) { }
