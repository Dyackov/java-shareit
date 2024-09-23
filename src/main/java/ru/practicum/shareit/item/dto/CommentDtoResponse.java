package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для ответа на запрос о комментарии.
 * Содержит информацию о комментарии, добавленном к вещи.
 */
@Builder
@Data
public class CommentDtoResponse {

    /**
     * Уникальный идентификатор комментария.
     */
    private Long id;

    /**
     * Текст комментария.
     */
    private String text;

    /**
     * Уникальный идентификатор вещи, к которой добавлен комментарий.
     */
    private Long itemId;

    /**
     * Имя автора комментария.
     */
    private String authorName;

    /**
     * Дата и время создания комментария.
     */
    private LocalDateTime created;
}
