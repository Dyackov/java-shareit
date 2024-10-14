package ru.practicum.shareit.request.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestClient;

/**
 * Контроллер для управления запросами на вещи.
 * <p>
 * Предоставляет REST API для создания и получения запросов на вещи,
 * а также для получения всех запросов и запросов по идентификатору.
 * </p>
 */
@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    /**
     * Создаёт новый запрос на вещь.
     *
     * @param userId           идентификатор пользователя, создающего запрос.
     * @param itemRequestDto   объект, содержащий данные о запросе на вещь.
     * @return объект ResponseEntity с результатом операции.
     */
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создание запроса на вещь:\n{}", itemRequestDto);
        ResponseEntity<Object> result = requestClient.createItemRequest(userId, itemRequestDto);
        log.info("Итог:\n{}", result);
        return result;
    }

    /**
     * Получает список всех запросов текущего пользователя вместе с данными об ответах на них.
     *
     * @param userId идентификатор пользователя.
     * @return объект ResponseEntity со списком запросов.
     */
    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка своих запросов вместе с данными об ответах на них. " +
                "ID пользователя: {}", userId);
        return requestClient.getOwnRequests(userId);
    }

    /**
     * Получает список всех запросов вместе с данными об ответах на них.
     *
     * @param userId идентификатор пользователя.
     * @return объект ResponseEntity со списком всех запросов.
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех запросов вместе с данными об ответах на них. " +
                "ID пользователя: {}", userId);
        return requestClient.getAllRequests(userId);
    }

    /**
     * Получает запрос по его идентификатору вместе с данными об ответах на него.
     *
     * @param userId    идентификатор пользователя, запрашивающего информацию.
     * @param requestId идентификатор запрашиваемого запроса.
     * @return объект ResponseEntity с данными о запросе.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long requestId) {
        log.info("Запрос на получение запроса вместе с данными об ответах на него. " +
                "ID пользователя: {}, ID запроса: {}", userId, requestId);
        return requestClient.getRequestsById(userId, requestId);
    }
}