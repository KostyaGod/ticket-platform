package dev.kostya.ticketplatform.auth.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException() {
        super("User is blocked");
    }
}
