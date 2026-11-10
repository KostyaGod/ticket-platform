package dev.kostya.ticketplatform.infrastructure.dto.response;

import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> errors
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null);
    }
}
