package dev.kostya.ticketplatform.event.dto.base;

public record Range <T extends Comparable<? super T>>(
        T from,
        T to
) { }
