package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса управления бронированиями.
 * <p>
 * Этот класс предоставляет методы для создания, получения и управления бронированиями.
 * Также включает проверки для обеспечения корректности входных данных и авторизации пользователей.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final JpaBookingRepository jpaBookingRepository;
    private final UserService userServiceImpl;
    private final ItemServiceImpl itemServiceImpl;

    /**
     * Создает новое бронирование.
     *
     * @param userId            Идентификатор пользователя, который делает бронирование.
     * @param bookingDtoRequest Объект, содержащий данные для создания бронирования.
     * @return Созданное бронирование в виде объекта BookingDto.
     */
    @Override
    public BookingDto createBooking(long userId, BookingDtoRequest bookingDtoRequest) {
        checkTime(bookingDtoRequest);
        User user = userServiceImpl.checkUserExist(userId);
        Item item = itemServiceImpl.checkItemExist(bookingDtoRequest.getItemId());
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Вы не можете создать бронирование с тем же идентификатором владельца.");
        } else if (!item.getAvailable()) {
            throw new ValidationException("Ошибка, вещь недоступна.");
        }
        Booking booking = Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        Booking bookingResultDao = jpaBookingRepository.save(booking);
        log.info("Создано бронирование DAO: \n{}", bookingResultDao);
        BookingDto bookingResultDto = BookingMapper.toBookingDto(bookingResultDao);
        log.info("Бронирование DAO: \n{}", bookingResultDto);
        return bookingResultDto;
    }

    /**
     * Подтверждает или отклоняет бронирование.
     *
     * @param userId    Идентификатор пользователя, который подтверждает или отклоняет бронирование.
     * @param bookingId Идентификатор бронирования, которое нужно подтвердить или отклонить.
     * @param approved  Статус подтверждения: true для подтверждения, false для отклонения.
     * @return Обновленное бронирование в виде объекта BookingDto.
     */
    @Override
    public BookingDto confirmOrRejectBooking(long userId, long bookingId, boolean approved) {
        Booking booking = checkBookingExist(bookingId);
        if (userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException("Ошибка авторизации.");
        } else if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже подтверждено.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("Бронирование подтверждено. BookingStatus: {}", booking.getStatus());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Бронирование отклонено. BookingStatus: {}", booking.getStatus());
        }
        jpaBookingRepository.save(booking);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        log.info("Статус бронирования изменен. ID бронирования: {}, Статус: {}", bookingId, bookingDto.getStatus());
        return bookingDto;
    }

    /**
     * Получает данные о конкретном бронировании по его идентификатору.
     *
     * @param userId    Идентификатор пользователя, запрашивающего данные о бронировании.
     * @param bookingId Идентификатор запрашиваемого бронирования.
     * @return Объект BookingDto с данными о бронировании.
     */
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        userServiceImpl.checkUserExist(userId);
        Booking booking = checkBookingExist(bookingId);
        checkUserAuthorizationForBooking(userId, booking);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        log.info("Получено бронирование:\n{}", bookingDto);
        return bookingDto;
    }

    /**
     * Получает список всех бронирований текущего пользователя по указанному состоянию.
     *
     * @param userId Идентификатор пользователя.
     * @param state  Состояние бронирования, по которому нужно получить список.
     * @return Список объектов BookingDto, соответствующих состоянию бронирования.
     */
    @Override
    public List<BookingDto> getBookingState(long userId, BookingState state) {
        userServiceImpl.checkUserExist(userId);
        log.info("Получен список бронирований арендатором. Состояние: {}, ID Пользователя: {}", state, userId);
        return switch (state) {
            case ALL -> toBookingDtoList(jpaBookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case CURRENT ->
                    toBookingDtoList(jpaBookingRepository.findAllByBookerStateCurrent(userId, LocalDateTime.now()));
            case PAST -> toBookingDtoList(jpaBookingRepository.findAllByBookerStatePast(userId, LocalDateTime.now()));
            case FUTURE ->
                    toBookingDtoList(jpaBookingRepository.findAllByBookerStateFuture(userId, LocalDateTime.now()));
            case WAITING ->
                    toBookingDtoList(jpaBookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING));
            case REJECTED ->
                    toBookingDtoList(jpaBookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED));
        };
    }

    /**
     * Получает список всех бронирований для всех вещей текущего пользователя по указанному состоянию.
     *
     * @param userId Идентификатор пользователя.
     * @param state  Состояние бронирования, по которому нужно получить список.
     * @return Список объектов BookingDto, соответствующих состоянию бронирования.
     */
    @Override
    public List<BookingDto> getAllByOwnerId(long userId, BookingState state) {
        userServiceImpl.checkUserExist(userId);
        log.info("Получен список бронирований владельцем вещей. Состояние: {}, ID Пользователя: {}", state, userId);
        return switch (state) {
            case ALL -> toBookingDtoList(jpaBookingRepository.findAllByOwnerId(userId));
            case CURRENT ->
                    toBookingDtoList(jpaBookingRepository.findAllByOwnerStateCurrent(userId, LocalDateTime.now()));
            case PAST -> toBookingDtoList(jpaBookingRepository.findAllByOwnerStatePast(userId, LocalDateTime.now()));
            case FUTURE ->
                    toBookingDtoList(jpaBookingRepository.findAllByOwnerStateFuture(userId, LocalDateTime.now()));
            case WAITING ->
                    toBookingDtoList(jpaBookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.WAITING));
            case REJECTED ->
                    toBookingDtoList(jpaBookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.REJECTED));
        };
    }

    /**
     * Проверяет существование бронирования по его идентификатору.
     *
     * @param bookingId Идентификатор проверяемого бронирования.
     * @return Объект Booking, если бронирование существует.
     */
    @Override
    public Booking checkBookingExist(long bookingId) {
        return jpaBookingRepository.findById(bookingId).orElseThrow(() -> {
            String errorMessage = "Бронирования с ID: " + bookingId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    /**
     * Проверяет авторизацию пользователя для работы с конкретным бронированием.
     *
     * @param userId  Идентификатор пользователя, который пытается получить доступ к бронированию.
     * @param booking Объект Booking, для которого нужно проверить авторизацию.
     */
    @Override
    public void checkUserAuthorizationForBooking(long userId, Booking booking) {
        log.info("Проверка авторизации. ID владельца: {}, ID бронирования: {}", userId, booking.getId());
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Ошибка авторизации.");
        }
    }

    /**
     * Преобразует список объектов Booking в список BookingDto.
     *
     * @param bookings Список объектов Booking.
     * @return Список объектов BookingDto.
     */
    private List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    /**
     * Проверяет временные рамки для бронирования.
     *
     * @param bookingDtoRequest Объект, содержащий данные для создания бронирования.
     */
    private void checkTime(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время конца не может быть раньше текущего времени.");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new ValidationException("Время конца не может быть раньше времени начала.");
        }
        if (bookingDtoRequest.getStart().isEqual(bookingDtoRequest.getEnd())) {
            throw new ValidationException("Время начала не может быть равно времени конца.");
        }
        if (bookingDtoRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала не может быть раньше текущего времени.");
        }
    }
}