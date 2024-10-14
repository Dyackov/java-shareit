package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления запросами на предметы.
 * Предоставляет методы для создания запросов, получения собственных и всех запросов,
 * а также получения запроса по идентификатору.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final UserService userServiceImpl;
    private final JpaItemRepository jpaItemRepository;

    /**
     * Создает новый запрос на предмет.
     *
     * @param userId         идентификатор пользователя, создающего запрос
     * @param itemRequestDto DTO объекта запроса на предмет
     * @return созданный запрос в виде DTO
     */
    @Override
    public ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userServiceImpl.checkUserExist(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        ItemRequest itemRequestResultDao = jpaItemRequestRepository.save(itemRequest);
        log.info("Создан запрос на вещь DAO:\n{}", itemRequestResultDao);
        ItemRequestDto itemRequestResultDto = ItemRequestMapper.toItemRequestDto(itemRequestResultDao);
        log.info("Запрос на вещь DTO:\n{}", itemRequestResultDto);
        return itemRequestResultDto;
    }

    /**
     * Получает список собственных запросов пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO объектов собственных запросов
     */
    @Override
    public List<ItemRequestDto> getOwnRequests(long userId) {
        userServiceImpl.checkUserExist(userId);
        List<ItemRequest> ownRequests = jpaItemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> itemRequestsDto = ownRequests.stream().map(ItemRequestMapper::toItemRequestDto).toList();
        List<Long> requestIds = ownRequests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<ItemDto>> itemsForRequest = getItemsForRequest(requestIds);
        setDetailsItemRequest(itemRequestsDto, itemsForRequest);
        return ownRequests.stream().map(ItemRequestMapper::toItemRequestDto).toList();
    }

    /**
     * Устанавливает детали запросов на предметы, включая связанные предметы.
     *
     * @param itemRequestsDto список DTO запросов на предметы
     * @param itemsForRequest отображение идентификаторов запросов на предметы и связанных предметов
     */
    private void setDetailsItemRequest(List<ItemRequestDto> itemRequestsDto, Map<Long, List<ItemDto>> itemsForRequest) {
        for (ItemRequestDto itemRequestDto : itemRequestsDto) {
            List<ItemDto> itemDtos = Optional.ofNullable(itemsForRequest.get(itemRequestDto.getId())).orElse(new ArrayList<>());
            itemRequestDto.setItems(itemDtos);
        }
    }

    /**
     * Получает предметы по идентификаторам запросов.
     *
     * @param requestIds список идентификаторов запросов
     * @return отображение идентификаторов запросов на предметы и связанных предметов
     */
    private Map<Long, List<ItemDto>> getItemsForRequest(List<Long> requestIds) {
        return jpaItemRepository.findAllByRequestIds(requestIds).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
    }

    /**
     * Получает список всех запросов, включая запросы других пользователей.
     *
     * @param userId идентификатор пользователя
     * @return список всех запросов в виде DTO
     */
    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        userServiceImpl.checkUserExist(userId);
        List<ItemRequestDto> requests = jpaItemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
        List<Long> requestIds = requests.stream().map(ItemRequestDto::getId).toList();
        Map<Long, List<ItemDto>> itemsForRequest = getItemsForRequest(requestIds);
        setDetailsItemRequest(requests, itemsForRequest);
        return requests;
    }

    /**
     * Получает запрос по идентификатору.
     *
     * @param userId    идентификатор пользователя
     * @param requestId идентификатор запроса
     * @return запрос в виде DTO
     */
    @Override
    public ItemRequestDto getRequestsById(long userId, long requestId) {
        userServiceImpl.checkUserExist(userId);
        ItemRequest itemRequest = checkItemRequestExist(requestId);

        List<Item> items = jpaItemRepository.findItemsByRequestId(itemRequest.getId());
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).toList();

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemsDto);
        return itemRequestDto;
    }

    /**
     * Проверяет существование запроса по его идентификатору.
     *
     * @param requestId идентификатор запроса
     * @return объект {@link ItemRequest}, если запрос существует
     * @throws NotFoundException если запрос с указанным идентификатором не найден
     */
    @Override
    public ItemRequest checkItemRequestExist(long requestId) {
        return jpaItemRequestRepository.findById(requestId).orElseThrow(() -> {
            String errorMessage = "Запроса с ID: " + requestId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });
    }
}