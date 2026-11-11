package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.ConflictException;

public class UsernameAlreadyExistsException extends ConflictException {
    public UsernameAlreadyExistsException() {
        super("User with this username already exists.");
    }
}
