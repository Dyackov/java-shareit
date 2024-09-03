package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.debug("Вещь сохранена в хранилище.\n{}", item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.debug("Вещь обновлена в хранилище.\n{}", item);
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        Item item = Optional.ofNullable(items.get(itemId)).orElseThrow(() -> {
            String errorMessage = "Вещи с ID: " + itemId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });
        log.debug("Вещь получена из хранилища. ID вещи {}", itemId);
        return item;
    }

    @Override
    public List<Item> getAllItemsFromUser(long userId) {
        log.debug("Получен список вещей из хранилища. ID владельца {}", userId);
        return new ArrayList<>(items.values().stream().filter(item -> item.getOwner() == userId).toList());
    }

    @Override
    public List<Item> getAllItems() {
        log.debug("Получен список всех вещей из хранилища.");
        return items.values().stream().toList();
    }

    @Override
    public void deleteItemById(long itemId) {
        items.remove(itemId);
        log.debug("Удалена вещь из хранилища. ID вещи: {}", itemId);
    }

    @Override
    public void deleteAllItemsByUser(long userId) {
        items.entrySet().removeIf(item -> item.getValue().getOwner() == userId);
        log.debug("Удалены все вещи из хранилища. ID владельца: {}", userId);
    }

    @Override
    public void deleteAllItems() {
        items.clear();
        log.debug("Удалены все вещи из хранилища.");
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}