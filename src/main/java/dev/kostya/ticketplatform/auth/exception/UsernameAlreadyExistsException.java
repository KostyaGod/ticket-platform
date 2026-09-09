package dev.kostya.ticketplatform.auth.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("User with this username already exists.");
    }
}
