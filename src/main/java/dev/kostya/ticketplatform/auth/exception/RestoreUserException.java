package dev.kostya.ticketplatform.auth.exception;

public class RestoreUserException extends RuntimeException {
    public RestoreUserException(String field) {
        super("Cannot restore user: '" +
                field +
                "' is already taken by another active user");
    }
}
