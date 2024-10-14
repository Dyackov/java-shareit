package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * DTO для запроса на создание комментария.
 * Содержит текст комментария, который будет добавлен к вещи.
 */
@Builder
public record CommentDtoRequest(String text) {
}