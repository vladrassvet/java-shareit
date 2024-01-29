package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleValidationException() {
        ValidationException validationException = new ValidationException("error");
        ErrorResponse errorResponse = errorHandler.handleValidationException(validationException);
        assertTrue(errorResponse != null);
        assertEquals(validationException.getMessage(), errorResponse.getError());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("error");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(notFoundException);
        assertEquals(notFoundException.getMessage(), errorResponse.getError());
    }
}
