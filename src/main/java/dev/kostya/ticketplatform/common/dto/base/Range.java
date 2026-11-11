package dev.kostya.ticketplatform.common.dto.base;

public record Range<T extends Comparable<? super T>>(
        T from,
        T to
) { }
