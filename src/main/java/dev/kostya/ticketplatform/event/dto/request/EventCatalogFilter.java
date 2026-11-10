package dev.kostya.ticketplatform.event.dto.request;

public record EventCatalogFilter(
        String name,
        String description,
        String category,
        Integer ageLimit
) { }
