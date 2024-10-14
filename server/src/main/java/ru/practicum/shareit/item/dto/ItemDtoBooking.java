package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.util.List;

/**
 * DTO для представления вещи с информацией о бронированиях и комментариях.
 * Содержит данные о вещи, её доступности и связанных бронированиях.
 */
@Builder
@Data
public class ItemDtoBooking {

    /**
     * Уникальный идентификатор вещи.
     */
    private Long id;

    /**
     * Название вещи.
     */
    private String name;

    /**
     * Описание вещи.
     */
    private String description;

    /**
     * Доступность вещи для аренды.
     */
    private Boolean available;

    /**
     * Запрос на создание вещи, если он имеется.
     */
    private Long requestId;

    /**
     * Последнее бронирование вещи.
     */
    private BookingDtoItem lastBooking;

    /**
     * Следующее бронирование вещи.
     */
    private BookingDtoItem nextBooking;

    /**
     * Список комментариев, связанных с вещью.
     */
    private List<CommentDtoResponse> comments;
}