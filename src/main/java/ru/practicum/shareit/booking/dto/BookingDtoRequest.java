package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) для передачи данных запроса на создание нового бронирования.
 * Используется для получения информации от клиента при создании бронирования через API.
 * <p>
 * Класс содержит следующую информацию:
 * - ID вещи, которую пользователь хочет забронировать
 * - Время начала бронирования
 * - Время окончания бронирования
 * </p>
 * <p>
 * Все поля обязательны для заполнения.
 * При валидации полей используется аннотация {@link NotNull} для предотвращения
 * отправки некорректных данных.
 * </p>
 */
@Builder
@Data
public class BookingDtoRequest {

    /**
     * Уникальный идентификатор вещи, которую хотят забронировать.
     * Не может быть null.
     */
    @NotNull(message = "ID вещи не может быть null.")
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     * Не может быть null.
     */
    @NotNull(message = "Время начала бронирования не может быть null.")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     * Не может быть null.
     */
    @NotNull(message = "Время окончания бронирования не может быть null.")
    private LocalDateTime end;
}
