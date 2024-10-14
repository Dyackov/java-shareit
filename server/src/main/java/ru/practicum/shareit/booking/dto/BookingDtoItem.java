package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) для представления краткой информации о бронировании вещи.
 * Используется для передачи основных данных о бронировании между клиентом и сервером,
 * например, при запросе списка вещей с их состояниями бронирования.
 * <p>
 * Класс содержит следующую информацию:
 * - ID бронирования
 * - Время начала бронирования
 * - Время окончания бронирования
 * - ID пользователя, который забронировал вещь
 * - Статус бронирования (например, WAITING, APPROVED, REJECTED)
 * </p>
 */
@Builder
@Data
public class BookingDtoItem {

    /**
     * Уникальный идентификатор бронирования.
     */
    private Long id;

    /**
     * Дата и время начала бронирования.
     */
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    private LocalDateTime end;

    /**
     * ID пользователя, который забронировал вещь.
     */
    private Long bookerId;

    /**
     * Статус бронирования (например, WAITING, APPROVED, REJECTED).
     */
    private BookingStatus status;
}
