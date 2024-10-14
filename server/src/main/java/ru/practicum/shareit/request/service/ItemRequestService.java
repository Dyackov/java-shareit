package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.error.exception.NotFoundException;

import java.util.List;

/**
 * Сервис для управления запросами на предметы.
 * Предоставляет методы для создания запросов,
 * получения собственных запросов и запросов по идентификатору.
 */
public interface ItemRequestService {

    /**
     * Создает новый запрос на предмет.
     *
     * @param userId         идентификатор пользователя, создающего запрос
     * @param itemRequestDto DTO объекта запроса на предмет
     * @return созданный запрос в виде DTO
     */
    ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto);

    /**
     * Получает список собственных запросов пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO объектов собственных запросов
     */
    List<ItemRequestDto> getOwnRequests(long userId);

    /**
     * Получает список всех запросов, включая собственные и запросы других пользователей.
     *
     * @param userId идентификатор пользователя
     * @return список всех запросов в виде DTO
     */
    List<ItemRequestDto> getAllRequests(long userId);

    /**
     * Получает запрос по идентификатору.
     *
     * @param userId    идентификатор пользователя
     * @param requestId идентификатор запроса
     * @return запрос в виде DTO
     */
    ItemRequestDto getRequestsById(long userId, long requestId);

    /**
     * Проверяет существование запроса по его идентификатору.
     *
     * @param requestId идентификатор запроса
     * @return объект {@link ItemRequest}, если запрос существует
     * @throws NotFoundException если запрос с указанным идентификатором не найден
     */
    ItemRequest checkItemRequestExist(long requestId);
}