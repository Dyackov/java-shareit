package ru.practicum.shareit.error.exception;

/**
 * Исключение, выбрасываемое при отсутствии прав доступа для выполнения операции.
 * Используется для обработки случаев, когда пользователь пытается получить доступ
 * к ресурсам или выполнять действия, для которых у него недостаточно прав.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Конструктор исключения.
     *
     * @param message Сообщение об ошибке, которое будет передано вместе с исключением.
     */
    public ForbiddenException(String message) {
        super(message);
    }
}

