package dev.kostya.ticketplatform.auth.security;

public record Tokens (
    String access,
    String refresh
) {}
