package dev.kostya.ticketplatform.auth.exception;

public class AdminSelfModificationException extends RuntimeException {
    public AdminSelfModificationException(String message) {super(message);}
    public AdminSelfModificationException() {
        super("You cannot update your own profile through the admin " +
                "management endpoint. Please use the profile settings page.");
    }
}
