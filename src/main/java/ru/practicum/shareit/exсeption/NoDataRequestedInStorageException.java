package ru.practicum.shareit.exсeption;

public class NoDataRequestedInStorageException extends RuntimeException {
    public NoDataRequestedInStorageException(String message) {
        super(message);
    }
}
