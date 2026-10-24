package dev.kostya.ticketplatform.auth.dto.request;

import dev.kostya.ticketplatform.auth.dto.base.EntityMetadataFilter;
import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.enums.UserStatus;

public record UserFilter(
        String username,
        String email,
        Role role,
        UserStatus userStatus,
        EntityMetadataFilter metadataFilter
) { }
