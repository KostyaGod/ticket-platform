package dev.kostya.ticketplatform.auth.dto.base;

public record Range<T extends Comparable<? super T>>(
        T from,
        T to
) { }
