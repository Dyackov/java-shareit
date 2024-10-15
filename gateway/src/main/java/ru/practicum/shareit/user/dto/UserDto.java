package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
     * Не может быть пустым.
     */
    @NotNull(message = "Имя не может быть пустым.")
    private String name;

    /**
     * Электронная почта пользователя.
     * Не может быть пустой, должна содержать символ '@' и не может содержать пробелы.
     */
    @Email(message = "Электронная почта должна содержать символ @.")
    @NotBlank(message = "Некорректный формат электронной почты.")
    private String email;
}