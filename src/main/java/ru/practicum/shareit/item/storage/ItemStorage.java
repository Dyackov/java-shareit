package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс для хранилища вещей.
 * Определяет операции создания, обновления, удаления и получения информации о вещах.
 */
public interface ItemStorage {

    /**
     * Создает новую вещь и сохраняет её в хранилище.
     * Присваивает вещи уникальный идентификатор.
     *
     * @param item Вещь для создания
     * @return Созданная вещь с присвоенным ID
     */
    Item createItem(Item item);

    /**
     * Обновляет данные существующей вещи в хранилище.
     * Если вещь с таким идентификатором не найдена, она будет создана или обновлена.
     *
     * @param item Вещь с обновленными данными
     * @return Обновленная вещь
     */
    Item updateItem(Item item);

    /**
     * Получает вещь по её уникальному идентификатору.
     * Если вещь с указанным идентификатором не найдена, выбрасывается {@link ru.practicum.shareit.error.exception.NotFoundException}.
     *
     * @param itemId Уникальный идентификатор вещи
     * @return Вещь с указанным идентификатором
     * @throws ru.practicum.shareit.error.exception.NotFoundException если вещь с указанным идентификатором не найдена
     */
    Item getItemById(long itemId);

    /**
     * Получает список всех вещей, принадлежащих пользователю с указанным идентификатором.
     *
     * @param userId Уникальный идентификатор владельца вещей
     * @return Список вещей, принадлежащих пользователю
     */
    List<Item> getAllItemsFromUser(long userId);

    /**
     * Получает список всех вещей, хранящихся в хранилище.
     *
     * @return Список всех вещей
     */
    List<Item> getAllItems();

    /**
     * Удаляет вещь по её уникальному идентификатору.
     *
     * @param itemId Уникальный идентификатор вещи
     */
    void deleteItemById(long itemId);

    /**
     * Удаляет все вещи, принадлежащие пользователю с указанным идентификатором.
     *
     * @param userId Уникальный идентификатор владельца вещей для удаления
     */
    void deleteAllItemsByUser(long userId);

    /**
     * Удаляет все вещи из хранилища.
     */
    void deleteAllItems();

}