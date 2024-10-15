package ru.practicum.shareit.item.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

/**
 * Контроллер для управления вещами в системе ShareIt.
 * <p>
 * Предоставляет REST API для выполнения операций с вещами, включая создание,
 * обновление, получение и удаление вещей, а также поиск доступных для аренды
 * вещей. Все запросы обрабатываются с учетом идентификатора пользователя,
 * который отправляет запрос.
 * </p>
 */
@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Valid
public class ItemController {
    private final ItemClient itemClient;

    /**
     * Создаёт новую вещь в системе.
     *
     * @param userId  идентификатор пользователя, создающего вещь.
     * @param itemDto объект, содержащий данные о создаваемой вещи.
     * @return объект ResponseEntity с результатом операции.
     */
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на создание вещи: \n{}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    /**
     * Обновляет информацию о существующей вещи.
     *
     * @param userId  идентификатор пользователя, обновляющего вещь.
     * @param itemId  идентификатор обновляемой вещи.
     * @param itemDto объект, содержащий новые данные о вещи.
     * @return объект ResponseEntity с результатом операции.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи. ID владельца: {}, ID вещи: {}.\nНовые данные: {}", userId, itemId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    /**
     * Получает информацию о вещи по её идентификатору.
     *
     * @param userId  идентификатор пользователя, запрашивающего информацию.
     * @param itemId  идентификатор запрашиваемой вещи.
     * @return объект ResponseEntity с данными о вещи.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Запрос на получение вещи. ID пользователя:{}, ID вещи: {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    /**
     * Получает список всех вещей текущего пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return объект ResponseEntity со списком вещей.
     */
    @GetMapping
    public ResponseEntity<Object> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех вещей владельца. ID владельца: {}", userId);
        return itemClient.getAllItemsFromUser(userId);
    }

    /**
     * Ищет доступные для аренды вещи по заданному тексту.
     *
     * @param userId идентификатор пользователя.
     * @param text   текст для поиска.
     * @return объект ResponseEntity со списком найденных вещей.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItemsByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam String text) {
        log.info("Запрос на поиск доступных для аренды вещей. Текст поиска: {}", text);
        return itemClient.searchAvailableItemsByText(userId, text);
    }

    /**
     * Удаляет вещь по её идентификатору.
     *
     * @param userId идентификатор пользователя, удаляющего вещь.
     * @param itemId идентификатор удаляемой вещи.
     * @return объект ResponseEntity с результатом операции.
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Запрос на удаление вещи. ID владельца: {}, ID вещи: {}.", userId, itemId);
        return itemClient.deleteItemById(userId, itemId);
    }

    /**
     * Удаляет все вещи текущего пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return объект ResponseEntity с результатом операции.
     */
    @DeleteMapping()
    public ResponseEntity<Object> deleteAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на удаление всех вещей. ID пользователя: {}", userId);
        return itemClient.deleteAllItemsByUser(userId);
    }

    /**
     * Создаёт комментарий к вещи.
     *
     * @param authorId           идентификатор пользователя, оставляющего комментарий.
     * @param itemId             идентификатор вещи, к которой оставляется комментарий.
     * @param commentDtoRequest объект, содержащий данные комментария.
     * @return объект ResponseEntity с результатом операции.
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                                @PathVariable long itemId,
                                                @RequestBody CommentDtoRequest commentDtoRequest) {
        log.info("Запрос на создание комментария вещи. ID пользователя: {}, ID вещи: {}, Комментарий:\n{}",
                authorId, itemId, commentDtoRequest);
        return itemClient.createComment(authorId, itemId, commentDtoRequest);
    }
}