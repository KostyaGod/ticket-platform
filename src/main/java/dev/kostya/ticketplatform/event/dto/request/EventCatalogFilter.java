package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.enums.EntityStatus;

public record EventCatalogFilter(
        Long cityId,
        Long venueId,
        EntityStatus status,
        String title,
        String description,
        String category,
        Integer ageLimit,
        String organizer
) { }
