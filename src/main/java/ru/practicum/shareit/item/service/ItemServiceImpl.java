package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage inMemoryItemStorage;
    private final UserServiceImpl userServiceImpl;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        userServiceImpl.getUserByIdDto(userId);
        item.setOwner(userId);
        Item itemResultDao = inMemoryItemStorage.createItem(item);
        log.info("Создана вещь DAO: \n{}", itemResultDao);
        ItemDto itemResultDto = ItemMapper.toItemDto(itemResultDao);
        log.info("Вещь DTO: \n{}", itemResultDto);
        return itemResultDto;
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = inMemoryItemStorage.getItemById(itemId);
        checkAuthorization(userId, item);
        log.info("Старая вещь DAO: \n{}", item);
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
        log.info("Обновлённая вещь DAO: \n{}", item);
        ItemDto resultDto = ItemMapper.toItemDto(inMemoryItemStorage.updateItem(item));
        log.info("Обновлённая вещь DTO: \n{}", resultDto);
        return resultDto;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Получена вещь. ID вещи: {}", itemId);
        return ItemMapper.toItemDto(inMemoryItemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsFromUser(long userId) {
        userServiceImpl.getUserByIdDto(userId);
        log.info("Получен список вещей. ID владельца: {}.", userId);
        return inMemoryItemStorage.getAllItemsFromUser(userId).stream().map(ItemMapper::toItemDto).toList();
    }

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

    @Override
    public void deleteItemById(long userId, long itemId) {
        checkAuthorization(userId, inMemoryItemStorage.getItemById(itemId));
        inMemoryItemStorage.deleteItemById(itemId);
        log.info("Удалена вещь. ID владельца: {}, ID вещи: {}", userId, itemId);
    }

    @Override
    public void deleteAllItemsByUser(long userId) {
        userServiceImpl.getUserByIdDto(userId);
        inMemoryItemStorage.deleteAllItemsByUser(userId);
        log.info("Удалены все вещи. ID владельца: {}", userId);
    }

    @Override
    public void deleteAllItems() {
        inMemoryItemStorage.deleteAllItems();
        log.info("Удалены все вещи.");
    }

    private void checkAuthorization(long userId, Item item) {
        userServiceImpl.getUserByIdDto(userId);
        log.debug("Проверка авторизации. ID владельца: {}, ID вещи: {}", userId, item.getId());
        if (userId != item.getOwner()) {
            throw new NotFoundException("Ошибка авторизации.");
        }
    }

}
