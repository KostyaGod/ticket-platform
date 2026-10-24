package dev.kostya.ticketplatform.infrastructure.dto.base;

public record Range<T extends Comparable<? super T>>(
        T from,
        T to
) { }
