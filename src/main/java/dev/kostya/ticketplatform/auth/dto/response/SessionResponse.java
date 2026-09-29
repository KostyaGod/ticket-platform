package dev.kostya.ticketplatform.auth.dto.response;

import java.util.UUID;

public record SessionResponse(
        UUID sessionId,
        long createdAt,
        long expiresAt,
        boolean current
) { }
