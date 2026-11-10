package dev.kostya.ticketplatform.infrastructure.exception;

public class EntityNotDeletedException extends RuntimeException {
    public EntityNotDeletedException(String entityName, Long id) {
        super(entityName + " with id " + id + " was not deleted.");
    }
}
