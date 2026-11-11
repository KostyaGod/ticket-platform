package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.EntityNotDeletedException;

public class UserNotDeletedException extends EntityNotDeletedException {
    public UserNotDeletedException(Long id) {
        super("User", id);
    }
}
