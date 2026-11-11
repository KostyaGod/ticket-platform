package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.BadRequestException;

public class NewPasswordMatchesOldException extends BadRequestException {
    public NewPasswordMatchesOldException() {
        super("The new password cannot be the same as your current password.");
    }
}
