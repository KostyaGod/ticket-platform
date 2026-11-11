package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.common.exception.EntityNotFoundException;

public class SessionNotFoundException extends EntityNotFoundException {
    public SessionNotFoundException() {
        super("Session not found or access denied.");
    }
}
