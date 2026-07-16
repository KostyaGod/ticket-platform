package dev.kostya.ticketplatform.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Session {
    private final UUID sessionId;
    private final Long userId;
    private UUID jti;
    private final long createdAt;
    private final long expiresAt;
}
