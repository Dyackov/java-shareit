package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Класс UserDto представляет собой Data Transfer Object (DTO) для передачи данных о пользователе.
 * <p>
 * Этот класс используется для передачи данных между слоями приложения и содержит валидируемые поля.
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
     * <p>
     * Это обязательное поле. Не должно быть пустым.
     */
    @NotNull(message = "Имя пользователя не может быть пустым.")
    private String name;

    /**
     * Электронная почта пользователя.
     * <p>
     * Это обязательное поле. Должно содержать символ '@' и не может быть пустым или содержать пробелы.
     */
    @NotNull(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна содержать символ @.")
    @NotBlank(message = "Электронная почта не может содержать пробелы.")
    private String email;

}
