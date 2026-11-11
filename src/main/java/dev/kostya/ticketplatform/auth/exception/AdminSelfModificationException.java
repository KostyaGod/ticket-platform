package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.BadRequestException;

public class AdminSelfModificationException extends BadRequestException {
    public AdminSelfModificationException(String message) {
        super(message);
    }

    public AdminSelfModificationException() {
        super("You cannot update your own profile through the admin management endpoint. Please use the profile settings page.");
    }
}
