package ru.practicum.shareit.booking.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingClient;

/**
 * Контроллер для управления бронированиями в приложении ShareIt.
 * Обрабатывает запросы, связанные с созданием, подтверждением/отклонением и просмотром бронирований.
 */
@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Valid
public class BookingController {
    private final BookingClient bookingClient;

    /**
     * Создает новое бронирование.
     *
     * @param userId идентификатор пользователя, создающего бронирование
     * @param bookingDtoRequest объект запроса на бронирование, содержащий детали бронирования
     * @return ResponseEntity с информацией о созданном бронировании
     */
    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        log.info("Запрос на бронирование вещи: \n{}", bookingDtoRequest);
        return bookingClient.createBooking(userId, bookingDtoRequest);
    }

    /**
     * Подтверждает или отклоняет бронирование.
     *
     * @param userId идентификатор пользователя, подтверждающего или отклоняющего бронирование
     * @param bookingId идентификатор бронирования
     * @param approved статус подтверждения (true - подтвердить, false - отклонить)
     * @return ResponseEntity с информацией о результате операции
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @PathVariable long bookingId,
                                                         @RequestParam boolean approved) {
        log.info("Запрос на подтверждение или отклонение бронирование вещи:\n" +
                "ID владельца: {}, ID бронирования: {}, BookingStatus: {}", userId, bookingId, approved);
        return bookingClient.confirmOrRejectBooking(userId, bookingId, approved);
    }

    /**
     * Получает информацию о конкретном бронировании по его идентификатору.
     *
     * @param userId идентификатор пользователя, запрашивающего информацию о бронировании
     * @param bookingId идентификатор бронирования
     * @return ResponseEntity с информацией о запрашиваемом бронировании
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        log.info("Запрос на получение данных о конкретном бронировании. ID пользователя: {}, ID бронирования: {}",
                userId, bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    /**
     * Получает список всех бронирований текущего пользователя с учетом состояния.
     *
     * @param userId идентификатор пользователя, запрашивающего список бронирований
     * @param state состояние бронирований (например, ALL, PAST, CURRENT, FUTURE)
     * @return ResponseEntity со списком бронирований текущего пользователя
     */
    @GetMapping()
    public ResponseEntity<Object> getAllByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение списка всех бронирований текущего пользователя. ID пользователя: {}, " +
                "Состояние: {}", userId, state);
        BookingState bookingState = BookingState.fromString(state);
        return bookingClient.getAllByBookerIdAndState(userId, bookingState);
    }

    /**
     * Получает список бронирований для всех вещей текущего пользователя с учетом состояния.
     *
     * @param userId идентификатор пользователя, запрашивающего список бронирований
     * @param state состояние бронирований (например, ALL, PAST, CURRENT, FUTURE)
     * @return ResponseEntity со списком бронирований для всех вещей текущего пользователя
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя. " +
                "ID пользователя: {}, Состояние: {}", userId, state);
        BookingState bookingState = BookingState.fromString(state);
        return bookingClient.getAllByBookerId(userId, bookingState);
    }
}
