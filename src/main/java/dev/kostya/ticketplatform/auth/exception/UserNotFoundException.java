package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super("User", id);
    }
}
