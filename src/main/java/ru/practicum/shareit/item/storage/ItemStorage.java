package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    List<Item> getAllItemsFromUser(long userId);

    List<Item> getAllItems();

    void deleteItemById(long itemId);

    void deleteAllItemsByUser(long userId);

    void deleteAllItems();

}
