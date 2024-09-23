package ru.practicum.shareit.booking.model.enums;

/**
 * Перечисление (Enum), представляющее возможные статусы бронирования.
 * <p>
 * Это перечисление используется для указания текущего статуса бронирования
 * и позволяет отслеживать его состояние в процессе обработки запросов.
 * </p>
 * <ul>
 *     <li>{@link #WAITING} — бронирование ожидает подтверждения владельцем вещи.</li>
 *     <li>{@link #APPROVED} — бронирование подтверждено владельцем вещи и активно.</li>
 *     <li>{@link #REJECTED} — бронирование отклонено владельцем вещи.</li>
 *     <li>{@link #CANCELLED} — бронирование отменено пользователем или системой.</li>
 * </ul>
 */
public enum BookingStatus {
    /**
     * Бронирование ожидает подтверждения владельцем вещи.
     */
    WAITING,

    /**
     * Бронирование подтверждено и активно.
     */
    APPROVED,

    /**
     * Бронирование отклонено владельцем вещи.
     */
    REJECTED,

    /**
     * Бронирование отменено пользователем или системой.
     */
    CANCELLED
}
