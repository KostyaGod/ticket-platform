package dev.kostya.ticketplatform.infrastructure.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(Long id) {
        super("User with this " + id + " does not exist.");
    }
}
