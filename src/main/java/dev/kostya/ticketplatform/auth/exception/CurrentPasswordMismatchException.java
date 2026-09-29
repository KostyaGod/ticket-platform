package dev.kostya.ticketplatform.auth.exception;

public class CurrentPasswordMismatchException extends RuntimeException {
    public CurrentPasswordMismatchException() {
        super("Incorrect current password. Please try again.");
    }
}
