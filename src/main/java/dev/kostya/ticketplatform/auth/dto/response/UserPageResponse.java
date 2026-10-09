package dev.kostya.ticketplatform.auth.dto.response;

import java.util.List;

public record UserPageResponse (
        List<UserResponse> users,
        PageResponse pageMeta
) { }
