package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException() {
        super("User with this email already exists");
    }
}
