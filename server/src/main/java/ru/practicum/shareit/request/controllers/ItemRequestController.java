package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * Контроллер для управления запросами на вещи в приложении ShareIt.
 * Обрабатывает запросы, связанные с созданием и получением запросов на вещи.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestServiceImpl;

    /**
     * Создает новый запрос на вещь.
     *
     * @param userId         ID пользователя, создающего запрос (передается в заголовке).
     * @param itemRequestDto DTO, содержащее информацию о запросе на вещь.
     * @return DTO с информацией о созданном запросе.
     */
    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса на вещь:\n{}", itemRequestDto);
        return itemRequestServiceImpl.createItemRequest(userId, itemRequestDto);
    }

    /**
     * Получает список собственных запросов текущего пользователя.
     *
     * @param userId ID текущего пользователя (передается в заголовке).
     * @return Список DTO с информацией о собственных запросах пользователя.
     */
    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка своих запросов вместе с данными об ответах на них. " +
                "ID пользователя: {}", userId);
        return itemRequestServiceImpl.getOwnRequests(userId);
    }

    /**
     * Получает список всех запросов вместе с данными об ответах на них.
     *
     * @param userId ID текущего пользователя (передается в заголовке).
     * @return Список DTO с информацией о всех запросах.
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка всех запросов вместе с данными об ответах на них. " +
                "ID пользователя: {}", userId);
        return itemRequestServiceImpl.getAllRequests(userId);
    }

    /**
     * Получает информацию о запросе по его ID.
     *
     * @param userId    ID пользователя, запрашивающего информацию (передается в заголовке).
     * @param requestId ID запроса, информацию о котором нужно получить.
     * @return DTO с информацией о запрашиваемом запросе.
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestsById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("Запрос на получение запроса вместе с данными об ответах на него. " +
                "ID пользователя: {}, ID запроса: {}", userId, requestId);
        return itemRequestServiceImpl.getRequestsById(userId, requestId);
    }
}