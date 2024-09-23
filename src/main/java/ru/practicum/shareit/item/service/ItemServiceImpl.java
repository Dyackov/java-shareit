package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaCommentRepository;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления вещами.
 * Этот класс предоставляет функционал для создания, обновления,
 * получения и удаления вещей, а также для работы с комментариями к ним.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final LocalDateTime NOW = LocalDateTime.now();
    private final JpaItemRepository jpaItemRepository;
    private final UserServiceImpl userServiceImpl;
    private final JpaBookingRepository jpaBookingRepository;
    private final JpaCommentRepository jpaCommentRepository;

    /**
     * Создает новую вещь и сохраняет её в базе данных.
     *
     * @param userId  идентификатор пользователя, создающего вещь
     * @param itemDto DTO объекта вещи, который необходимо создать
     * @return созданная вещь в виде DTO
     */
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userServiceImpl.checkUserExist(userId);
        item.setOwner(user);
        Item itemResultDao = jpaItemRepository.save(item);
        log.info("Создана вещь DAO: \n{}", itemResultDao);
        ItemDto itemResultDto = ItemMapper.toItemDto(itemResultDao);
        log.info("Вещь DTO: \n{}", itemResultDto);
        return itemResultDto;
    }

    /**
     * Обновляет существующую вещь.
     *
     * @param userId  идентификатор пользователя, который пытается обновить вещь
     * @param itemId  идентификатор обновляемой вещи
     * @param itemDto DTO объекта вещи с обновленными данными
     * @return обновленная вещь в виде DTO
     */
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = checkItemExist(itemId);
        userServiceImpl.checkUserExist(userId);
        checkUserAuthorizationForItem(userId, item);
        log.info("Старая вещь DAO: \n{}", item);
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
        log.info("Обновлённая вещь DAO: \n{}", item);
        ItemDto resultDto = ItemMapper.toItemDto(jpaItemRepository.save(item));
        log.info("Обновлённая вещь DTO: \n{}", resultDto);
        return resultDto;
    }

    /**
     * Получает информацию о вещи по её идентификатору.
     *
     * @param userId идентификатор пользователя, запрашивающего информацию о вещи
     * @param itemId идентификатор вещи
     * @return DTO объекта вещи с информацией о бронированиях и комментариях
     */
    @Override
    public ItemDtoBooking getItemById(long userId, long itemId) {
        Item item = checkItemExist(itemId);
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        if (item.getOwner().getId() == userId) {
            itemDtoBooking.setLastBooking(BookingMapper.toBookingDtoItem
                    (jpaBookingRepository.findLastBookingByBookerId(itemDtoBooking.getId(), LocalDateTime.now())
                            .orElse(null)));
            itemDtoBooking.setNextBooking(BookingMapper.toBookingDtoItem
                    (jpaBookingRepository.findNextBookingByBookerId(itemDtoBooking.getId(), LocalDateTime.now())
                            .orElse(null)));
        }
        jpaCommentRepository.findCommentsByItemId(itemId).ifPresent(comment ->
                itemDtoBooking.setComments(comment.stream().map(CommentMapper::toCommentDtoResponse).toList()));
        log.info("Получена вещь. ID вещи: {}", itemId);
        return itemDtoBooking;
    }

    /**
     * Получает список всех вещей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список вещей в виде DTO
     */
    @Override
    public List<ItemDtoBooking> getAllItemsFromUser(long userId) {
        userServiceImpl.checkUserExist(userId);
        List<Item> items = jpaItemRepository.findAllByOwnerId(userId);
        List<ItemDtoBooking> itemDtoBookings = items.stream().map(ItemMapper::toItemDtoBooking).toList();
        for (ItemDtoBooking itemDtoBooking : itemDtoBookings) {
            itemDtoBooking.setLastBooking(BookingMapper.toBookingDtoItem
                    (jpaBookingRepository.findLastBookingByBookerId(itemDtoBooking.getId(), LocalDateTime.now())
                            .orElse(null)));
            itemDtoBooking.setNextBooking(BookingMapper.toBookingDtoItem
                    (jpaBookingRepository.findNextBookingByBookerId(itemDtoBooking.getId(), LocalDateTime.now())
                            .orElse(null)));
            jpaCommentRepository.findCommentsByItemId(itemDtoBooking.getId()).ifPresent(comment ->
                    itemDtoBooking.setComments(comment.stream().map(CommentMapper::toCommentDtoResponse).toList()));
        }
        log.info("Получен список вещей. ID владельца: {}.", userId);
        return itemDtoBookings;
    }

    /**
     * Ищет доступные вещи по текстовому запросу.
     *
     * @param text текст для поиска
     * @return список найденных вещей в виде DTO
     */
    @Override
    public List<ItemDto> searchAvailableItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            log.warn("Не указан текст для поиска.");
            return List.of();
        }
        List<Item> allItems = jpaItemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(text, text);
        List<ItemDto> resultSearch = allItems.stream().map(ItemMapper::toItemDto).toList();
        log.info("Получен список вещей. Текст поиска: {} \n{}", text, resultSearch);
        return resultSearch;
    }

    /**
     * Удаляет вещь по её идентификатору.
     *
     * @param userId идентификатор пользователя, который пытается удалить вещь
     * @param itemId идентификатор удаляемой вещи
     */
    @Override
    public void deleteItemById(long userId, long itemId) {
        Item item = checkItemExist(itemId);
        userServiceImpl.checkUserExist(userId);
        checkUserAuthorizationForItem(userId, item);
        jpaItemRepository.delete(item);
        log.info("Удалена вещь. ID владельца: {}, ID вещи: {}", userId, itemId);
    }

    /**
     * Удаляет все вещи пользователя.
     *
     * @param userId идентификатор пользователя
     */
    @Override
    public void deleteAllItemsByUser(long userId) {
        userServiceImpl.checkUserExist(userId);
        jpaItemRepository.deleteAllByOwnerId(userId);
        log.info("Удалены все вещи. ID владельца: {}", userId);
    }

    /**
     * Удаляет все вещи из базы данных.
     */
    @Override
    public void deleteAllItems() {
        jpaItemRepository.deleteAll();
        log.info("Удалены все вещи.");
    }

    /**
     * Проверяет существование вещи по её идентификатору.
     *
     * @param itemId идентификатор вещи
     * @return объект вещи
     * @throws NotFoundException если вещь не найдена
     */
    @Override
    public Item checkItemExist(long itemId) {
        return jpaItemRepository.findById(itemId).orElseThrow(() -> {
            String errorMessage = "Вещи с ID: " + itemId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    /**
     * Проверяет авторизацию пользователя для работы с вещью.
     *
     * @param userId идентификатор пользователя
     * @param item   объект вещи
     * @throws ForbiddenException если пользователь не является владельцем вещи
     */
    @Override
    public void checkUserAuthorizationForItem(long userId, Item item) {
        log.info("Проверка авторизации. ID владельца: {}, ID вещи: {}", userId, item.getId());
        if (userId != item.getOwner().getId()) {
            throw new ForbiddenException("Ошибка авторизации.");
        }
    }

    /**
     * Создает новый комментарий к вещи.
     *
     * @param authorId          идентификатор автора комментария
     * @param itemId            идентификатор вещи, к которой оставляется комментарий
     * @param commentDtoRequest DTO объекта комментария
     * @return созданный комментарий в виде DTO
     */
    @Override
    public CommentDtoResponse createComment(long authorId, long itemId, CommentDtoRequest commentDtoRequest) {
        log.info("Начало метода createComment");
        User author = userServiceImpl.checkUserExist(authorId);
        Item item = checkItemExist(itemId);
        checkAuthorAuthorizationForItem(authorId, itemId);
        Comment comment = Comment.builder()
                .text(commentDtoRequest.text())
                .item(item)
                .author(author)
                .created(NOW)
                .build();
        jpaCommentRepository.save(comment);
        log.info("Создан комментарий DAO:\n{}", comment);
        CommentDtoResponse commentDtoResponse = CommentMapper.toCommentDtoResponse(comment);
        log.info("Комментарий DTO:\n{}", commentDtoResponse);
        return commentDtoResponse;
    }

    /**
     * Проверяет авторизацию автора для оставления отзыва о вещи.
     *
     * @param bookerId идентификатор арендатора
     * @param itemId   идентификатор вещи
     * @throws ValidationException если авторизация не пройдена
     */
    public void checkAuthorAuthorizationForItem(long bookerId, long itemId) {
        jpaBookingRepository.checkItemReviewAuthorizationAfterRental(bookerId, itemId, LocalDateTime.now()).orElseThrow(() -> {
            String errorMessage = "Не пройдена авторизация. ID booker: " + bookerId + ", ID item: " + itemId;
            log.warn("Ошибка: {}", errorMessage);
            return new ValidationException(errorMessage);
        });
    }
}