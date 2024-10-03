package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) для передачи информации о бронировании.
 * Используется для представления данных бронирования между клиентом и сервером.
 * <p>
 * Данный класс инкапсулирует основную информацию о бронировании:
 * - ID бронирования
 * - Дата и время начала бронирования
 * - Дата и время окончания бронирования
 * - Вещь, которая бронируется
 * - Пользователь, который сделал бронирование
 * - Статус бронирования
 * </p>
 */
@Builder
@Data
public class BookingDto {

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
     * Вещь, которая бронируется.
     */
    private Item item;

    /**
     * Пользователь, который забронировал вещь.
     */
    private User booker;

    /**
     * Статус бронирования (например, WAITING, APPROVED, REJECTED).
     */
    private BookingStatus status;
}
