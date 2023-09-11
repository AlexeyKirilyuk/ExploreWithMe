package ru.practicum.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String object, Long objectId) {
        super(String.format("%s id %d not found.", object, objectId));
    }
}
