package ru.practicum.shareit.error.handler;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UnsupportedStateException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.error.model.ErrorResponse;
import ru.practicum.shareit.error.model.ErrorStateResponse;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void notFoundTest() {
        NotFoundException exception = new NotFoundException("Not found");
        ErrorResponse response = errorHandler.notFound(exception);

        assertThat(response.getError()).isEqualTo("Не найдено");

    }

    @Test
    void validateHttpMessageNotReadableTest() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid message");
        ErrorResponse response = errorHandler.validate(exception);

        assertThat(response.getError()).isEqualTo("Ошибка.");

    }

    @Test
    void handleValidationExceptionTest() {
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("objectName", "fieldName", "Field error message"));

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response.getError()).isEqualTo("Ошибка валидации.");
    }

    @Test
    void handleDataIntegrityViolationTest() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Database error");
        ErrorResponse response = errorHandler.handleDataIntegrityViolationException(exception);

        assertThat(response.getError()).isEqualTo("Ошибка при обработке данных.");
    }

    @Test
    void validateRequestBookingTest() {
        ValidationException exception = new ValidationException("Booking error");
        ErrorResponse response = errorHandler.validateRequestBooking(exception);

        assertThat(response.getError()).isEqualTo("Ошибка.");
    }

    @Test
    void authorizationUserTest() {
        ForbiddenException exception = new ForbiddenException("Forbidden");
        ErrorResponse response = errorHandler.authorizationUser(exception);

        assertThat(response.getError()).isEqualTo("Ошибка.");
    }

    @Test
    void handleUnsupportedStateExceptionTest() {
        UnsupportedStateException exception = new UnsupportedStateException("Unsupported state");
        ErrorStateResponse response = errorHandler.handleUnsupportedStateException(exception);

        assertThat(response.error()).isEqualTo("Unsupported state");
    }
}