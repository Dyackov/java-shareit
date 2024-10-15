package ru.practicum.shareit.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UnsupportedStateException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.error.model.ErrorResponse;
import ru.practicum.shareit.error.model.ErrorStateResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик исключений, отвечающий за централизованное управление ошибками,
 * возникающими в приложении.
 * Позволяет обрабатывать различные типы исключений и возвращать
 * соответствующие сообщения об ошибках в формате {@link ErrorResponse}.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обработка исключения NotFoundException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NotFoundException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponse("Не найдено", exception.getMessage());
    }

    /**
     * Обработка исключения HttpMessageNotReadableException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validate(final HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponse("Ошибка.", exception.getMessage());
    }

    /**
     * Обработка исключения MethodArgumentNotValidException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке валидации.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn(exception.getMessage());
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce("", (acc, error) -> acc + error);
        return new ErrorResponse("Ошибка валидации.", errorMessage.trim());
    }

    /**
     * Обработка исключения MissingRequestHeaderException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validateRequest(final MissingRequestHeaderException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorResponse("Ошибка.", description);
    }

    /**
     * Обработка исключения DataIntegrityViolationException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке базы данных.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String message = "Ошибка базы данных: " + exception.getMostSpecificCause().getMessage();
        log.error("DataIntegrityViolationException: {}", message);
        return new ErrorResponse("Ошибка при обработке данных.", message);
    }

    /**
     * Обработка исключения ValidationException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validateRequestBooking(final ValidationException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorResponse("Ошибка.", description);
    }

    /**
     * Обработка исключения ForbiddenException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse authorizationUser(final ForbiddenException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorResponse("Ошибка.", description);
    }

    /**
     * Обработка исключения UnsupportedStateException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorStateResponse handleUnsupportedStateException(final UnsupportedStateException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorStateResponse(description);
    }
}
