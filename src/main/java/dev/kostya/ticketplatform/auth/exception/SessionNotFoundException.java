package dev.kostya.ticketplatform.auth.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() { super("Session not found or access denied."); }
}
