package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItemsFromUser(long userId);

    List<ItemDto> searchAvailableItemsByText(String text);

    void deleteItemById(long userId, long itemId);

    void deleteAllItemsByUser(long userId);

    void deleteAllItems();

}
