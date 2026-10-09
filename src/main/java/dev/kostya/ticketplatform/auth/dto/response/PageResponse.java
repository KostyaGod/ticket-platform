package dev.kostya.ticketplatform.auth.dto.response;

public record PageResponse(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) { }
