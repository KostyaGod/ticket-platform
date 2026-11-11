package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
