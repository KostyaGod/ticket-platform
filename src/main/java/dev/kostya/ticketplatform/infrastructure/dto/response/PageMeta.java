package dev.kostya.ticketplatform.infrastructure.dto.response;

public record PageMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) { }
