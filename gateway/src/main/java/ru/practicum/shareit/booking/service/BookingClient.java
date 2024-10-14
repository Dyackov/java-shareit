package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

/**
 * Клиент для взаимодействия с API бронирований в приложении ShareIt.
 * <p>
 * Этот сервис предоставляет методы для создания, подтверждения/отклонения бронирования,
 * а также для получения информации о бронированиях пользователя и его вещей.
 * </p>
 */
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    /**
     * Конструктор для инициализации клиента с заданным URL сервера и шаблоном запросов.
     *
     * @param serverUrl URL сервера ShareIt
     * @param builder   объект для построения RestTemplate
     */
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Создает новое бронирование.
     *
     * @param userId            уникальный идентификатор пользователя, создающего бронирование
     * @param bookingDtoRequest данные запроса на создание бронирования
     * @return ResponseEntity с результатом операции
     */
    public ResponseEntity<Object> createBooking(long userId, BookingDtoRequest bookingDtoRequest) {
        return post("", userId, bookingDtoRequest);
    }

    /**
     * Подтверждает или отклоняет бронирование.
     *
     * @param userId    уникальный идентификатор пользователя, подтверждающего или отклоняющего бронирование
     * @param bookingId уникальный идентификатор бронирования
     * @param approved  статус подтверждения (true - подтвердить, false - отклонить)
     * @return ResponseEntity с результатом операции
     */
    public ResponseEntity<Object> confirmOrRejectBooking(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    /**
     * Получает информацию о конкретном бронировании по его идентификатору.
     *
     * @param userId    уникальный идентификатор пользователя, запрашивающего информацию
     * @param bookingId уникальный идентификатор бронирования
     * @return ResponseEntity с данными о бронировании
     */
    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    /**
     * Получает все бронирования текущего пользователя по указанному состоянию.
     *
     * @param userId уникальный идентификатор пользователя
     * @param state  состояние бронирования для фильтрации
     * @return ResponseEntity с списком бронирований
     */
    public ResponseEntity<Object> getAllByBookerIdAndState(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("?state={state}", userId, parameters);
    }

    /**
     * Получает все бронирования для всех вещей текущего пользователя по указанному состоянию.
     *
     * @param userId уникальный идентификатор пользователя
     * @param state  состояние бронирования для фильтрации
     * @return ResponseEntity с списком бронирований
     */
    public ResponseEntity<Object> getAllByBookerId(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("/owner?state={state}", userId, parameters);
    }
}