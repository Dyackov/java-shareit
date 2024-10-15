package ru.practicum.shareit.error.exception;

/**
 * Исключение, выбрасываемое при использовании неподдерживаемого состояния.
 * Используется для обработки случаев, когда переданное состояние не соответствует
 * ожидаемым или допустимым значениям.
 */
public class UnsupportedStateException extends RuntimeException {

    /**
     * Конструктор исключения.
     *
     * @param message Сообщение об ошибке, которое будет передано вместе с исключением.
     */
    public UnsupportedStateException(String message) {
        super(message);
    }
}