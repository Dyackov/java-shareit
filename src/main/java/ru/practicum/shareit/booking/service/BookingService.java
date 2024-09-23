package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;

import java.util.List;

/**
 * Интерфейс для сервиса управления бронированиями.
 * <p>
 * Этот интерфейс определяет методы для создания, получения и управления
 * бронированиями, а также проверки их существования и авторизации пользователей.
 * </p>
 */
public interface BookingService {

    /**
     * Создает новое бронирование.
     *
     * @param userId            Идентификатор пользователя, который делает бронирование.
     * @param bookingDtoRequest Объект, содержащий данные для создания бронирования.
     * @return Созданное бронирование в виде объекта BookingDto.
     */
    BookingDto createBooking(long userId, BookingDtoRequest bookingDtoRequest);

    /**
     * Подтверждает или отклоняет бронирование.
     *
     * @param userId    Идентификатор пользователя, который подтверждает или отклоняет бронирование.
     * @param bookingId Идентификатор бронирования, которое нужно подтвердить или отклонить.
     * @param approved  Статус подтверждения: true для подтверждения, false для отклонения.
     * @return Обновленное бронирование в виде объекта BookingDto.
     */
    BookingDto confirmOrRejectBooking(long userId, long bookingId, boolean approved);

    /**
     * Получает данные о конкретном бронировании по его идентификатору.
     *
     * @param userId    Идентификатор пользователя, запрашивающего данные о бронировании.
     * @param bookingId Идентификатор запрашиваемого бронирования.
     * @return Объект BookingDto с данными о бронировании.
     */
    BookingDto getBookingById(long userId, long bookingId);

    /**
     * Получает список всех бронирований текущего пользователя по указанному состоянию.
     *
     * @param userId Идентификатор пользователя.
     * @param state  Состояние бронирования, по которому нужно получить список.
     * @return Список объектов BookingDto, соответствующих состоянию бронирования.
     */
    List<BookingDto> getBookingState(long userId, BookingState state);

    /**
     * Получает список всех бронирований для всех вещей текущего пользователя по указанному состоянию.
     *
     * @param userId Идентификатор пользователя.
     * @param state  Состояние бронирования, по которому нужно получить список.
     * @return Список объектов BookingDto, соответствующих состоянию бронирования.
     */
    List<BookingDto> getAllByOwnerId(long userId, BookingState state);

    /**
     * Проверяет существование бронирования по его идентификатору.
     *
     * @param bookingId Идентификатор проверяемого бронирования.
     * @return Объект Booking, если бронирование существует.
     */
    Booking checkBookingExist(long bookingId);

    /**
     * Проверяет авторизацию пользователя для работы с конкретным бронированием.
     *
     * @param userId  Идентификатор пользователя, который пытается получить доступ к бронированию.
     * @param booking Объект Booking, для которого нужно проверить авторизацию.
     */
    void checkUserAuthorizationForBooking(long userId, Booking booking);
}