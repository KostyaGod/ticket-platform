package dev.kostya.ticketplatform.auth.dto.response;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        UserStatus status,
        LocalDateTime createdAt
) {
}
