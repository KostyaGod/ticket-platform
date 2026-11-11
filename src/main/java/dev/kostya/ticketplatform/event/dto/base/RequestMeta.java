package dev.kostya.ticketplatform.event.dto.base;

import java.time.ZoneId;

public record RequestMeta(
        Long cityId,
        Long organizationId,
        ZoneId timezone
) {
}
