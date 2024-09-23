package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.InMemoryUserService;

import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link ItemService}, предоставляющая операции с вещами.
 * Включает создание, обновление, удаление, получение и поиск доступных вещей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemService implements ItemService {

    private final ItemStorage inMemoryItemStorage;
    private final InMemoryUserService inMemoryUserService;

    /**
     * Создает новую вещь и сохраняет её в хранилище.
     * Проверяет, существует ли пользователь с указанным ID перед созданием вещи.
     *
     * @param userId  Уникальный идентификатор пользователя, создающего вещь
     * @param itemDto DTO для создания новой вещи
     * @return DTO созданной вещи с присвоенным ID
     */
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        inMemoryUserService.getUserByIdDto(userId);
        User user = UserMapper.toUser(inMemoryUserService.getUserByIdDto(userId));
        item.setOwner(user);
        Item itemResultDao = inMemoryItemStorage.createItem(item);
        log.info("Создана вещь DAO: \n{}", itemResultDao);
        ItemDto itemResultDto = ItemMapper.toItemDto(itemResultDao);
        log.info("Вещь DTO: \n{}", itemResultDto);
        return itemResultDto;
    }

    /**
     * Обновляет данные существующей вещи.
     * Проверяет права на редактирование на основе ID пользователя.
     *
     * @param userId  Уникальный идентификатор пользователя, пытающегося обновить вещь
     * @param itemId  Уникальный идентификатор вещи, которую нужно обновить
     * @param itemDto DTO с обновленными данными вещи
     * @return DTO обновленной вещи
     * @throws NotFoundException если пользователь не авторизован для редактирования вещи
     */
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = inMemoryItemStorage.getItemById(itemId);
        checkAuthorization(userId, item);
        log.info("Старая вещь DAO : \n{}", item);
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
        log.info("Обновлённая вещь DAO: \n{}", item);
        ItemDto resultDto = ItemMapper.toItemDto(inMemoryItemStorage.updateItem(item));
        log.info("Обновлённая вещь DTO: \n{}", resultDto);
        return resultDto;
    }

    /**
     * Получает вещь по её уникальному идентификатору.
     *
     * @param itemId Уникальный идентификатор вещи
     * @return DTO найденной вещи
     */
    @Override
    public ItemDtoBooking getItemById(long userId, long itemId) {
        log.info("Получена вещь. ID вещи: {}", itemId);
        return ItemMapper.toItemDtoBooking(inMemoryItemStorage.getItemById(itemId));
    }

    /**
     * Получает список всех вещей, принадлежащих пользователю с указанным идентификатором.
     *
     * @param userId Уникальный идентификатор владельца вещей
     * @return Список DTO всех вещей пользователя
     */
    @Override
    public List<ItemDtoBooking> getAllItemsFromUser(long userId) {
        inMemoryUserService.getUserByIdDto(userId);
        log.info("Получен список вещей. ID владельца: {}.", userId);
        return inMemoryItemStorage.getAllItemsFromUser(userId).stream().map(ItemMapper::toItemDtoBooking).toList();
    }

    /**
     * Выполняет поиск доступных вещей по тексту.
     * Ищет в названии и описании вещи.
     *
     * @param text Текст для поиска
     * @return Список DTO найденных доступных вещей
     */
    @Override
    public List<ItemDto> searchAvailableItemsByText(String text) {
        List<Item> allItems = inMemoryItemStorage.getAllItems();
        if (text == null || text.isEmpty()) {
            log.warn("Не указан текст для поиска.");
            return List.of();
        }
        List<ItemDto> resultSearch = allItems.stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase())
                        || item.getDescription().toUpperCase().contains(text.toUpperCase()))
                .map(ItemMapper::toItemDto).toList();
        log.info("Получен список вещей. Текст поиска: {} \n{}", text, resultSearch);
        return resultSearch;
    }

    /**
     * Удаляет вещь по её уникальному идентификатору.
     * Проверяет права на удаление на основе ID пользователя.
     *
     * @param userId Уникальный идентификатор пользователя, пытающегося удалить вещь
     * @param itemId Уникальный идентификатор вещи для удаления
     * @throws NotFoundException если пользователь не авторизован для удаления вещи
     */
    @Override
    public void deleteItemById(long userId, long itemId) {
        checkAuthorization(userId, inMemoryItemStorage.getItemById(itemId));
        inMemoryItemStorage.deleteItemById(itemId);
        log.info("Удалена вещь. ID владельца: {}, ID вещи: {}", userId, itemId);
    }

    /**
     * Удаляет все вещи, принадлежащие пользователю с указанным идентификатором.
     *
     * @param userId Уникальный идентификатор владельца вещей для удаления
     */
    @Override
    public void deleteAllItemsByUser(long userId) {
        inMemoryUserService.getUserByIdDto(userId);
        inMemoryItemStorage.deleteAllItemsByUser(userId);
        log.info("Удалены все вещи. ID владельца: {}", userId);
    }

    /**
     * Удаляет все вещи из хранилища.
     */
    @Override
    public void deleteAllItems() {
        inMemoryItemStorage.deleteAllItems();
        log.info("Удалены все вещи.");
    }

    @Override
    public Item checkItemExist(long itemId) {
        return null;
    }

    @Override
    public void checkUserAuthorizationForItem(long userId, Item item) {

    }

    @Override
    public CommentDtoResponse createComment(long userId, long itemId, CommentDtoRequest commentDtoRequest) {
        return null;
    }

    /**
     * Проверяет права на редактирование или удаление вещи.
     * Выбрасывает исключение {@link NotFoundException}, если пользователь не является владельцем вещи.
     *
     * @param userId Уникальный идентификатор пользователя
     * @param item   Вещь, для которой проверяется авторизация
     */
    private void checkAuthorization(long userId, Item item) {
        inMemoryUserService.getUserByIdDto(userId);
        log.debug("Проверка авторизации. ID владельца: {}, ID вещи: {}", userId, item.getId());
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Ошибка авторизации.");
        }
    }

}