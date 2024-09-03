package ru.practicum.shareit.item.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на создание вещи: \n{}", itemDto);
        return itemServiceImpl.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи. ID владельца: {}, ID вещи: {}.\nНовые данные: {}", userId, itemId, itemDto);
        return itemServiceImpl.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("Запрос на получение вещи. ID вещи: {}", itemId);
        return itemServiceImpl.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех вещей владельца. ID владельца: {}", userId);
        return itemServiceImpl.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItemsByText(@RequestParam String text) {
        log.info("Запрос на поиск доступных для аренды вещей. Текст поиска: {}", text);
        return itemServiceImpl.searchAvailableItemsByText(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Запрос на удаление вещи. ID владельца: {}, ID вещи: {}.", userId, itemId);
        itemServiceImpl.deleteItemById(userId, itemId);
    }

    @DeleteMapping()
    public void deleteAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на удаление всех вещей. ID пользователя: {}", userId);
        itemServiceImpl.deleteAllItemsByUser(userId);
    }

}
