package ru.practicum.shareit.booking.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * Контроллер для управления бронированиями в приложении ShareIt.
 * Обрабатывает запросы, связанные с созданием, подтверждением/отклонением и просмотром бронирований.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;

    /**
     * Создает новое бронирование для вещи.
     *
     * @param userId            ID пользователя, который бронирует вещь (передается в заголовке).
     * @param bookingDtoRequest DTO, содержащее информацию о бронировании.
     * @return DTO с информацией о созданном бронировании.
     */
    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        log.info("Запрос на бронирование вещи: \n{}", bookingDtoRequest);
        return bookingServiceImpl.createBooking(userId, bookingDtoRequest);
    }

    /**
     * Подтверждает или отклоняет бронирование вещи владельцем.
     *
     * @param userId    ID владельца вещи (передается в заголовке).
     * @param bookingId ID бронирования, которое нужно подтвердить или отклонить.
     * @param approved  Параметр, указывающий, подтверждено бронирование или отклонено (true - подтверждено, false - отклонено).
     * @return DTO с обновленной информацией о бронировании.
     */
    @PatchMapping("/{bookingId}")
    public BookingDto confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        log.info("Запрос на подтверждение или отклонение бронирование вещи:\n" +
                "ID владельца: {}, ID бронирования: {}, BookingStatus: {}", userId, bookingId, approved);
        return bookingServiceImpl.confirmOrRejectBooking(userId, bookingId, approved);
    }

    /**
     * Получает информацию о конкретном бронировании по его ID.
     * <p>
     * Может быть выполнен либо автором бронирования, либо владельцем вещи.
     *
     * @param userId    ID пользователя, запрашивающего информацию (передается в заголовке).
     * @param bookingId ID бронирования, которое нужно получить.
     * @return DTO с информацией о запрашиваемом бронировании.
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        log.info("Запрос на получение данных о конкретном бронировании. ID пользователя: {}, ID бронирования: {}",
                userId, bookingId);
        return bookingServiceImpl.getBookingById(userId, bookingId);
    }

    /**
     * Получает список всех бронирований текущего пользователя по их состоянию.
     * <p>
     * Бронирования сортируются по дате от более новых к более старым.
     *
     * @param userId ID текущего пользователя (передается в заголовке).
     * @param state  Состояние бронирований (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED).
     * @return Список DTO с информацией о бронированиях.
     */
    @GetMapping()
    public List<BookingDto> getAllByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение списка всех бронирований текущего пользователя. ID пользователя: {}, " +
                "Состояние: {}", userId, state);
        BookingState bookingState = BookingState.fromString(state);
        return bookingServiceImpl.getBookingState(userId, bookingState);
    }

    /**
     * Получает список бронирований для всех вещей текущего пользователя (владельца).
     * <p>
     * Бронирования сортируются по дате от более новых к более старым.
     *
     * @param userId ID владельца вещей (передается в заголовке).
     * @param state  Состояние бронирований (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED).
     * @return Список DTO с информацией о бронированиях вещей текущего пользователя.
     */
    @GetMapping("/owner")
    public List<BookingDto> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя. " +
                "ID пользователя: {}, Состояние: {}", userId, state);
        BookingState bookingState = BookingState.fromString(state);
        return bookingServiceImpl.getAllByOwnerId(userId, bookingState);
    }
}
