package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) для представления запроса на предмет.
 * Содержит информацию о запросе, включая его идентификатор, описание,
 * дату создания и список предметов, связанных с этим запросом.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    /**
     * Уникальный идентификатор запроса.
     */
    private Long id;

    /**
     * Описание запроса.
     */
    private String description;

    /**
     * Дата и время создания запроса.
     */
    private LocalDateTime created;

    /**
     * Список предметов, связанных с запросом.
     */
    private List<ItemDto> items = new ArrayList<>();
}