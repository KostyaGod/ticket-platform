package dev.kostya.ticketplatform.event.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " with this " + id + " does not exist.");
    }
}
