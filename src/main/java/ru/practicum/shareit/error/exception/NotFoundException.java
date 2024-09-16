package ru.practicum.shareit.error.exception;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый ресурс не найден.
 * <p>
 * Это исключение наследуется от {@link RuntimeException} и используется для указания на случаи,
 * когда запрашиваемые данные или ресурсы не существуют в системе.
 * Примеры использования включают отсутствие пользователя, вещи или других объектов в хранилище.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Конструктор класса {@code NotFoundException}.
     *
     * @param message Сообщение об ошибке, которое будет передано в конструктор суперкласса
     *                {@link RuntimeException}. Это сообщение описывает причину возникновения исключения.
     */
    public NotFoundException(String message) {
        super(message);
    }

}

