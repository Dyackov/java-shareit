package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс для управления операциями с вещами в системе.
 * Предоставляет методы для создания, обновления, удаления и поиска вещей,
 * а также для получения вещей, принадлежащих конкретному пользователю.
 */
public interface ItemService {

    /**
     * Создает новую вещь и сохраняет её в хранилище.
     * Ассоциирует вещь с указанным пользователем.
     *
     * @param userId  Уникальный идентификатор пользователя, создающего вещь
     * @param itemDto DTO, содержащий данные для создания новой вещи
     * @return DTO созданной вещи с присвоенным ID
     */
    ItemDto createItem(long userId, ItemDto itemDto);

    /**
     * Обновляет существующую вещь в хранилище.
     * Проверяет права на редактирование на основе ID пользователя.
     *
     * @param userId  Уникальный идентификатор пользователя, пытающегося обновить вещь
     * @param itemId  Уникальный идентификатор вещи, которую нужно обновить
     * @param itemDto DTO с обновленными данными вещи
     * @return DTO обновленной вещи
     */
    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    /**
     * Получает вещь по её уникальному идентификатору.
     *
     * @param itemId Уникальный идентификатор вещи
     * @return DTO найденной вещи
     */
    ItemDtoBooking getItemById(long userId, long itemId);

    /**
     * Получает список всех вещей, принадлежащих пользователю с указанным идентификатором.
     *
     * @param userId Уникальный идентификатор владельца вещей
     * @return Список DTO всех вещей пользователя
     */
    List<ItemDtoBooking> getAllItemsFromUser(long userId);

    /**
     * Выполняет поиск доступных вещей по тексту.
     * Ищет в названии и описании вещи.
     *
     * @param text Текст для поиска
     * @return Список DTO найденных доступных вещей
     */
    List<ItemDto> searchAvailableItemsByText(String text);

    /**
     * Удаляет вещь по её уникальному идентификатору.
     * Проверяет права на удаление на основе ID пользователя.
     *
     * @param userId Уникальный идентификатор пользователя, пытающегося удалить вещь
     * @param itemId Уникальный идентификатор вещи для удаления
     */
    void deleteItemById(long userId, long itemId);

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

    /**
     * Проверяет существование вещи по её идентификатору.
     *
     * @param itemId Уникальный идентификатор вещи
     * @return Объект модели {@link Item}
     */
    Item checkItemExist(long itemId);

    /**
     * Проверяет авторизацию пользователя для работы с вещью.
     *
     * @param userId Уникальный идентификатор пользователя
     * @param item Объект модели {@link Item}
     */
    void checkUserAuthorizationForItem(long userId, Item item);

    /**
     * Создает комментарий к вещи.
     *
     * @param userId Уникальный идентификатор пользователя, оставляющего комментарий
     * @param itemId Уникальный идентификатор вещи
     * @param commentDtoRequest DTO с данными комментария
     * @return DTO созданного комментария
     */
    CommentDtoResponse createComment(long userId, long itemId, CommentDtoRequest commentDtoRequest);
}
