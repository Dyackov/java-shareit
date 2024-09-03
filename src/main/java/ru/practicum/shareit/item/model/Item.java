package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * Представляет собой модель данных для вещи.
 * Содержит информацию о вещи, включая её идентификатор, название, описание, доступность,
 * владельца и запрос, с которым вещь может быть связана.
 */
@Data
@Builder
public class Item {

    /**
     * Уникальный идентификатор вещи.
     * Устанавливается системой при создании вещи.
     */
    private Long id;

    /**
     * Название вещи.
     * Например, "Сверло" или "Книга по Java".
     */
    private String name;

    /**
     * Описание вещи.
     * Дополнительная информация о вещи, такая как её состояние или особенности.
     */
    private String description;

    /**
     * Доступность вещи.
     * Если значение истинно, вещь доступна для аренды; если ложно, вещь недоступна.
     */
    private Boolean available;

    /**
     * Идентификатор владельца вещи.
     * Указывает на пользователя, который является владельцем этой вещи.
     */
    private Long owner;

    /**
     * Запрос, с которым может быть связана вещь.
     * Это может быть текст запроса, который был создан пользователем и к которому привязана вещь.
     */
    private String request;

}
