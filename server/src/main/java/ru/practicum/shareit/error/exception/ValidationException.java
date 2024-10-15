package ru.practicum.shareit.error.exception;

/**
 * Исключение, выбрасываемое при возникновении ошибок валидации.
 * Используется для обработки случаев, когда входные данные не соответствуют
 * установленным требованиям или ограничениям.
 */
public class ValidationException extends RuntimeException {

    /**
     * Конструктор исключения.
     *
     * @param message Сообщение об ошибке, которое будет передано вместе с исключением.
     */
    public ValidationException(String message) {
        super(message);
    }
}
