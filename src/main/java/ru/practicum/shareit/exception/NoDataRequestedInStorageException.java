package ru.practicum.shareit.exception;

public class NoDataRequestedInStorageException extends RuntimeException {
    public NoDataRequestedInStorageException(String message) {
        super(message);
    }
}
