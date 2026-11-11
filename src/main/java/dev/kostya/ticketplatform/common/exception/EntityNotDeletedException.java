package dev.kostya.ticketplatform.common.exception;

public class EntityNotDeletedException extends ConflictException {
    public EntityNotDeletedException(String message) {
        super(message);
    }

    public EntityNotDeletedException(String entityName, Object id) {
        super(entityName + " with id " + id + " was not deleted.");
    }
}
