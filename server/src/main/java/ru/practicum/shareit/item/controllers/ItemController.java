package ru.practicum.shareit.item.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

/**
 * Контроллер для управления вещами в системе.
 * Предоставляет REST API для создания, обновления, получения и удаления вещей,
 * а также для поиска доступных для аренды вещей.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;

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
     * @return {@link ItemDtoBooking} с данными вещи
     */
    @GetMapping("/{itemId}")
    public ItemDtoBooking getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Запрос на получение вещи. ID пользователя:{}, ID вещи: {}", userId, itemId);
        return itemServiceImpl.getItemById(userId, itemId);
    }

    /**
     * Получает список всех вещей, принадлежащих пользователю.
     *
     * @param userId уникальный идентификатор пользователя
     * @return список {@link ItemDtoBooking} всех вещей пользователя
     */
    @GetMapping
    public List<ItemDtoBooking> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех вещей владельца. ID владельца: {}", userId);
        return itemServiceImpl.getAllItemsFromUser(userId);
    }

    /**
     * Ищет доступные для аренды вещи по заданному тексту.
     *
     * @param text текст для поиска в названии и описании вещей
     * @return список {@link ItemDto} доступных для аренды вещей, соответствующих тексту поиска
     */

    //TODO:  getway добавил X-Sharer-User-Id
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

    /**
     * Создает комментарий для вещи.
     *
     * @param authorId          уникальный идентификатор пользователя, оставляющего комментарий
     * @param itemId            уникальный идентификатор вещи
     * @param commentDtoRequest DTO, содержащий данные комментария
     * @return созданный {@link CommentDtoResponse} с информацией о комментарии
     */
    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                            @PathVariable long itemId,
                                            @RequestBody CommentDtoRequest commentDtoRequest) {
        log.info("Запрос на создание комментария вещи. ID пользователя: {}, ID вещи: {}, Комментарий:\n{}",
                authorId, itemId, commentDtoRequest);
        return itemServiceImpl.createComment(authorId, itemId, commentDtoRequest);
    }

}