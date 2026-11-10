package dev.kostya.ticketplatform.infrastructure.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " does not exist.");
    }
}
