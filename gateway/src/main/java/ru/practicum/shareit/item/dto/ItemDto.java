package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Объект передачи данных (DTO) для представления информации о вещи.
 * Используется для передачи данных между клиентом и сервером, а также для отображения информации о вещи.
 *
 * <p>DTO предназначен для того, чтобы избежать утечек внутренней модели данных и обеспечить только необходимый набор данных.
 * Включает валидацию полей, чтобы гарантировать корректность входных данных.</p>
 */
@Data
@Builder
public class ItemDto {

    /**
     * Уникальный идентификатор вещи.
     * Может быть null при создании нового объекта.
     */
    private Long id;

    /**
     * Название вещи.
     * Не может быть null или пустым.
     */
    @NotNull(message = "Имя вещи не может быть null.")
    @NotBlank(message = "Имя вещи не может быть пустым.")
    private String name;

    /**
     * Описание вещи.
     * Не может быть null или пустым.
     */
    @NotNull(message = "Описание не может быть null.")
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;

    /**
     * Статус доступности вещи.
     * Не может быть null.
     */
    @NotNull(message = "Статус не может быть пустым.")
    private Boolean available;

    /**
     * Дополнительная информация о запросе, с которым связана вещь.
     * Может быть null, если вещь не связана с запросом.
     */
    private Long requestId;
}