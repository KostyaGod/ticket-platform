package dev.kostya.ticketplatform.event.dto.response;

import java.time.Instant;

public record EventCatalogResponse(
        Long id,
        String title,
        String category,
        Integer ageLimit,
        Instant nextSessionStartAt
) { }
