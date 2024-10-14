package ru.practicum.shareit.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.UnsupportedStateException;
import ru.practicum.shareit.error.model.ErrorStateResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обработка исключения UnsupportedStateException.
     *
     * @param exception Исключение, которое нужно обработать.
     * @return {@link ErrorStateResponse} с сообщением об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorStateResponse handleUnsupportedStateException(final UnsupportedStateException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorStateResponse(description);
    }
}