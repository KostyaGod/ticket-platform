package dev.kostya.ticketplatform.infrastructure.dto.base;

public record RequestMeta(
        Long cityId,
        Long organizationId,
        String timezone
) {
}
