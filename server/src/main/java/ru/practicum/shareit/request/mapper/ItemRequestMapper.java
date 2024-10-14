package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Маппер для преобразования объектов {@link ItemRequest} в {@link ItemRequestDto} и наоборот.
 * Предоставляет статические методы для конвертации между моделью запроса и её DTO представлением.
 */
public class ItemRequestMapper {

    /**
     * Преобразует {@link ItemRequestDto} в {@link ItemRequest}.
     *
     * @param itemRequestDto DTO объекта запроса на предмет
     * @return объект {@link ItemRequest}, созданный на основе данных из {@link ItemRequestDto}
     */
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    /**
     * Преобразует {@link ItemRequest} в {@link ItemRequestDto}.
     *
     * @param itemRequest объект запроса на предмет
     * @return DTO объекта запроса на предмет, созданный на основе данных из {@link ItemRequest}
     */
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}