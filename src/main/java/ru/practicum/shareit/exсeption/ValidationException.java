package ru.practicum.shareit.exсeption;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
