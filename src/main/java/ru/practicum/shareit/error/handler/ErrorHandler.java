package ru.practicum.shareit.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.model.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс ErrorHandler предназначен для обработки исключений, возникающих в приложении.
 * <p>
 * Этот класс перехватывает исключения, выбрасываемые в контроллерах, и возвращает соответствующие HTTP-статусы и сообщения об ошибках.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обрабатывает исключение NotFoundException, выбрасываемое при попытке найти несуществующий ресурс.
     *
     * @param exception исключение NotFoundException.
     * @return объект ErrorResponse с сообщением об ошибке и статусом HTTP 404 (NOT FOUND).
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NotFoundException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponse("Не найдено", exception.getMessage());
    }

    /**
     * Обрабатывает исключение ConflictException, выбрасываемое при возникновении конфликта данных (например, при попытке создать пользователя с уже существующим email).
     *
     * @param exception исключение ConflictException.
     * @return объект ErrorResponse с сообщением об ошибке и статусом HTTP 409 (CONFLICT).
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(final ConflictException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponse("Ошибка.", exception.getMessage());
    }

    /**
     * Обрабатывает исключение HttpMessageNotReadableException, возникающее при неправильном формате запроса (например, при неверном формате JSON).
     *
     * @param exception исключение HttpMessageNotReadableException.
     * @return объект ErrorResponse с сообщением об ошибке и статусом HTTP 400 (BAD REQUEST).
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validate(final HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponse("Ошибка.", exception.getMessage());
    }

    /**
     * Обрабатывает исключение MethodArgumentNotValidException, выбрасываемое при нарушении валидации входных данных.
     * Собирает все ошибки валидации и возвращает их в виде строки.
     *
     * @param exception исключение MethodArgumentNotValidException.
     * @return объект ErrorResponse с сообщением о валидационной ошибке и статусом HTTP 400 (BAD REQUEST).
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn(exception.getMessage());
        return new ErrorResponse("Ошибка валидации.", errors.toString());
    }

    /**
     * Обрабатывает исключение MissingRequestHeaderException, возникающее при отсутствии необходимого заголовка в запросе.
     *
     * @param exception исключение MissingRequestHeaderException.
     * @return объект ErrorResponse с сообщением об ошибке и статусом HTTP 400 (BAD REQUEST).
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validateRequest(final MissingRequestHeaderException exception) {
        String description = exception.getMessage();
        log.warn(description);
        return new ErrorResponse("Ошибка.", description);
    }

}