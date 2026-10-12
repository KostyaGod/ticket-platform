package dev.kostya.ticketplatform.event.dto.request;

public record RequestMeta(
        Long cityId,
        Long organizationId,
        String timezone
) {
}
