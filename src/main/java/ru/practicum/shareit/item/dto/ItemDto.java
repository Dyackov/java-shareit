package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    @NotNull(message = "Имя вещи не может быть null.")
    @NotBlank(message = "Имя вещи не может быть пустым.")
    private String name;
    @NotNull(message = "Описание не может быть null.")
    @NotBlank(message = "Имя вещи не может быть пустым.")
    private String description;
    @NotNull(message = "Статус не может быть пустым.")
    private Boolean available;
    private String request;

}