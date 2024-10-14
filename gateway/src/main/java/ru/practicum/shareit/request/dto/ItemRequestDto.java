package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) для запроса на вещь.
 * <p>
 * Этот класс используется для передачи данных о запросе на вещь, включая
 * идентификатор, описание, дату создания и список связанных вещей.
 * </p>
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    /**
     * Идентификатор запроса.
     */
    private Long id;

    /**
     * Описание запроса. Не может быть пустым.
     */
    @NotNull(message = "Описание не может быть пустым.")
    private String description;

    /**
     * Дата и время создания запроса.
     */
    private LocalDateTime created;

    /**
     * Список вещей, связанных с запросом.
     */
    private List<ItemDto> items = new ArrayList<>();
}