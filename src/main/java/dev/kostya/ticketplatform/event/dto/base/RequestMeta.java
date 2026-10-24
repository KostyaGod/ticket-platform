package dev.kostya.ticketplatform.event.dto.base;

public record RequestMeta(
        Long cityId,
        Long organizationId,
        String timezone
) {
}
