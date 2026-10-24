package dev.kostya.ticketplatform.auth.exception;

public class UserNotDeletedException extends RuntimeException {
    public UserNotDeletedException(Long id) {
        super("User with id " + id + " is not deleted");
    }
}
