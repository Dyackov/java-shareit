package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Класс UserDto представляет собой Data Transfer Object (DTO) для передачи данных о пользователе.
 * <p>
 * Этот класс используется для передачи данных между слоями приложения, такими как
 * контроллеры и сервисы. Он содержит валидируемые поля для обеспечения корректности
 * вводимых данных.
 * </p>
 */
@Data
@Builder
public class UserDto {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя.
     */

    private String name;

    /**
     * Электронная почта пользователя.
     */
    private String email;
}