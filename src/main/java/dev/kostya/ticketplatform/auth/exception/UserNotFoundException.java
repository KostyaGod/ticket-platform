package dev.kostya.ticketplatform.auth.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with this " + id + " does not exist.");
    }
}
