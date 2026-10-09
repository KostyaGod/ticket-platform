package dev.kostya.ticketplatform.auth.dto.request;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.enums.UserStatus;

import java.time.LocalDateTime;

public record UserFilter(
        String username,
        String email,
        Role role,
        UserStatus status,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore
) { }
