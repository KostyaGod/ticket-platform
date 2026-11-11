package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.ConflictException;

public class RestoreUserException extends ConflictException {
    public RestoreUserException(String field) {
        super("Cannot restore user: '" + field + "' is already taken by another active user");
    }
}
