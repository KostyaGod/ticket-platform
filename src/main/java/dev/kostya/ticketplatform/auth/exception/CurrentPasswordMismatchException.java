package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.BadRequestException;

public class CurrentPasswordMismatchException extends BadRequestException {
    public CurrentPasswordMismatchException() {
        super("Incorrect current password. Please try again.");
    }
}
