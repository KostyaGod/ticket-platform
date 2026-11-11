package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.ForbiddenException;

public class UserBlockedException extends ForbiddenException {
    public UserBlockedException() {
        super("User is blocked");
    }
}
