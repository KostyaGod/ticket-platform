package dev.kostya.ticketplatform.event.dto.response;

public record PageMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) { }
