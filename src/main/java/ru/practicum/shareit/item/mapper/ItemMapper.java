package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Класс для преобразования объектов между слоями данных и представления.
 * Предоставляет методы для конвертации между сущностями модели {@link Item} и объектами данных передачи {@link ItemDto}.
 */
public class ItemMapper {

    /**
     * Преобразует объект модели {@link Item} в объект передачи данных {@link ItemDto}.
     *
     * @param item Объект модели {@link Item}, который необходимо преобразовать.
     * @return Объект передачи данных {@link ItemDto}, содержащий данные из {@link Item}.
     */
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    /**
     * Преобразует объект передачи данных {@link ItemDto} в объект модели {@link Item}.
     *
     * @param itemDto Объект передачи данных {@link ItemDto}, который необходимо преобразовать.
     * @return Объект модели {@link Item}, содержащий данные из {@link ItemDto}.
     */
    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }

}
