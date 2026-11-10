package dev.kostya.ticketplatform.auth.dto.response;

import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import dev.kostya.ticketplatform.auth.dto.base.EntityMetadata;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        UserStatus userStatus,
        EntityMetadata<User> metadata
) {
}
