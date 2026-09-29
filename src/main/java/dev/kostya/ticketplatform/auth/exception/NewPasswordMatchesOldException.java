package dev.kostya.ticketplatform.auth.exception;

public class NewPasswordMatchesOldException extends RuntimeException {
    public NewPasswordMatchesOldException() {super("The new password cannot be the same as your current password.");}
}
