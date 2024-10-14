package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.error.exception.UnsupportedStateException;

/**
 * Перечисление (Enum), представляющее возможные состояния бронирования.
 * <p>
 * Это перечисление используется для фильтрации и работы с различными состояниями бронирования,
 * которые могут быть указаны пользователем в запросах.
 * </p>
 * <ul>
 *     <li>{@link #ALL} — все бронирования, без фильтрации по состоянию.</li>
 *     <li>{@link #CURRENT} — бронирования, которые сейчас активны (в процессе аренды).</li>
 *     <li>{@link #PAST} — завершённые бронирования, срок аренды которых уже истёк.</li>
 *     <li>{@link #FUTURE} — бронирования, которые начнутся в будущем.</li>
 *     <li>{@link #WAITING} — бронирования, которые ожидают подтверждения.</li>
 *     <li>{@link #REJECTED} — отклонённые бронирования.</li>
 * </ul>
 */
public enum BookingState {
    /**
     * Все бронирования, без фильтрации по состоянию.
     */
    ALL,

    /**
     * Текущие бронирования, которые находятся в процессе аренды.
     */
    CURRENT,

    /**
     * Завершённые бронирования, срок аренды которых истёк.
     */
    PAST,

    /**
     * Будущие бронирования, которые ещё не начались.
     */
    FUTURE,

    /**
     * Бронирования, которые находятся в статусе ожидания подтверждения.
     */
    WAITING,

    /**
     * Отклонённые бронирования.
     */
    REJECTED;

    /**
     * Метод для преобразования строки в соответствующее состояние бронирования.
     *
     * @param state строковое представление состояния бронирования
     * @return соответствующее состояние бронирования
     * @throws UnsupportedStateException если передано неизвестное состояние
     */
    public static BookingState fromString(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }
}