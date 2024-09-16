package ru.practicum.shareit.item.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * Контроллер для обработки HTTP-запросов, связанных с вещами.
 * Предоставляет CRUD операции для управления вещами, такие как создание, обновление, получение и удаление.
 * Обрабатывает запросы и делегирует выполнение бизнес-логики в {@link ItemService}.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemServiceImpl;

    /**
     * Создает новую вещь.
     *
     * @param userId  уникальный идентификатор пользователя, создающего вещь
     * @param itemDto DTO, содержащий данные для создания вещи
     * @return созданный {@link ItemDto} с присвоенным ID
     */
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на создание вещи: \n{}", itemDto);
        return itemServiceImpl.createItem(userId, itemDto);
    }

    /**
     * Обновляет данные существующей вещи.
     *
     * @param userId  уникальный идентификатор пользователя, запрашивающего обновление
     * @param itemId  уникальный идентификатор вещи для обновления
     * @param itemDto DTO с новыми данными вещи
     * @return обновленный {@link ItemDto}
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи. ID владельца: {}, ID вещи: {}.\nНовые данные: {}", userId, itemId, itemDto);
        return itemServiceImpl.updateItem(userId, itemId, itemDto);
    }

    /**
     * Получает информацию о вещи по ее идентификатору.
     *
     * @param itemId уникальный идентификатор вещи
     * @return {@link ItemDto} с данными вещи
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("Запрос на получение вещи. ID вещи: {}", itemId);
        return itemServiceImpl.getItemById(itemId);
    }

    /**
     * Получает список всех вещей, принадлежащих пользователю.
     *
     * @param userId уникальный идентификатор пользователя
     * @return список {@link ItemDto} всех вещей пользователя
     */
    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех вещей владельца. ID владельца: {}", userId);
        return itemServiceImpl.getAllItemsFromUser(userId);
    }

    /**
     * Ищет доступные для аренды вещи по заданному тексту.
     *
     * @param text текст для поиска в названии и описании вещей
     * @return список {@link ItemDto} доступных для аренды вещей, соответствующих тексту поиска
     */
    @GetMapping("/search")
    public List<ItemDto> searchAvailableItemsByText(@RequestParam String text) {
        log.info("Запрос на поиск доступных для аренды вещей. Текст поиска: {}", text);
        return itemServiceImpl.searchAvailableItemsByText(text);
    }

    /**
     * Удаляет вещь по ее идентификатору.
     *
     * @param userId уникальный идентификатор пользователя, запрашивающего удаление
     * @param itemId уникальный идентификатор вещи для удаления
     */
    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Запрос на удаление вещи. ID владельца: {}, ID вещи: {}.", userId, itemId);
        itemServiceImpl.deleteItemById(userId, itemId);
    }

    /**
     * Удаляет все вещи пользователя.
     *
     * @param userId уникальный идентификатор пользователя
     */
    @DeleteMapping()
    public void deleteAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на удаление всех вещей. ID пользователя: {}", userId);
        itemServiceImpl.deleteAllItemsByUser(userId);
    }

}