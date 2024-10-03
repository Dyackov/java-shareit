package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

/**
 * Реализация хранилища вещей, использующая в качестве хранения данные в памяти.
 * Позволяет выполнять операции создания, обновления, удаления и получения информации о вещах.
 */
@Repository
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    /**
     * Создает новую вещь и сохраняет её в хранилище.
     * Присваивает вещи уникальный ID перед сохранением.
     *
     * @param item Вещь для создания
     * @return Созданная вещь с присвоенным ID
     */
    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.debug("Вещь сохранена в хранилище.\n{}", item);
        return item;
    }

    /**
     * Обновляет данные существующей вещи в хранилище.
     * Если вещь с таким ID не найдена, она будет создана или обновлена.
     *
     * @param item Вещь с обновленными данными
     * @return Обновленная вещь
     */
    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.debug("Вещь обновлена в хранилище.\n{}", item);
        return item;
    }

    /**
     * Получает вещь по её ID из хранилища.
     * Если вещь с указанным ID не найдена, выбрасывается {@link NotFoundException}.
     *
     * @param itemId ID вещи для получения
     * @return Вещь с указанным ID
     * @throws NotFoundException если вещь с указанным ID не найдена
     */
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

    /**
     * Получает список всех вещей, принадлежащих пользователю с указанным ID.
     *
     * @param userId ID владельца вещей
     * @return Список вещей, принадлежащих пользователю
     */
    @Override
    public List<Item> getAllItemsFromUser(long userId) {
        log.debug("Получен список вещей из хранилища. ID владельца {}", userId);
        return new ArrayList<>(items.values().stream().filter(item -> item.getOwner().getId() == userId).toList());
    }

    /**
     * Получает список всех вещей в хранилище.
     *
     * @return Список всех вещей
     */
    @Override
    public List<Item> getAllItems() {
        log.debug("Получен список всех вещей из хранилища.");
        return items.values().stream().toList();
    }

    /**
     * Удаляет вещь из хранилища по её ID.
     *
     * @param itemId ID вещи для удаления
     */
    @Override
    public void deleteItemById(long itemId) {
        items.remove(itemId);
        log.debug("Удалена вещь из хранилища. ID вещи: {}", itemId);
    }

    /**
     * Удаляет все вещи, принадлежащие пользователю с указанным ID.
     *
     * @param userId ID владельца вещей для удаления
     */
    @Override
    public void deleteAllItemsByUser(long userId) {
        items.entrySet().removeIf(item -> item.getValue().getOwner().getId() == userId);
        log.debug("Удалены все вещи из хранилища. ID владельца: {}", userId);
    }

    /**
     * Удаляет все вещи из хранилища.
     */
    @Override
    public void deleteAllItems() {
        items.clear();
        log.debug("Удалены все вещи из хранилища.");
    }

    /**
     * Генерирует уникальный идентификатор для новой вещи.
     * Использует максимальный существующий ID и увеличивает его на единицу.
     *
     * @return Уникальный идентификатор для новой вещи
     */
    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}