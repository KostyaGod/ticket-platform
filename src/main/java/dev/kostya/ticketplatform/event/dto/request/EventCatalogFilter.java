package dev.kostya.ticketplatform.event.dto.request;

public record EventCatalogFilter(
        String title,
        String description,
        String category,
        Integer ageLimit
) { }
