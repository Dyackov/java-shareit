package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * Клиент для взаимодействия с API запросов на вещи.
 * <p>
 * Этот класс предоставляет методы для создания запросов на вещи и получения
 * информации о существующих запросах. Он использует REST API
 * для выполнения HTTP-запросов к серверу.
 * </p>
 */
@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    /**
     * Конструктор класса, инициализирующий клиент с заданным URL сервера и конфигурацией RestTemplate.
     *
     * @param serverUrl URL сервера, к которому будет производиться обращение.
     * @param builder   Строитель для создания экземпляра RestTemplate.
     */
    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Создаёт новый запрос на вещь.
     *
     * @param userId         Идентификатор пользователя, создающего запрос.
     * @param itemRequestDto DTO запроса на вещь, содержащий данные о запросе.
     * @return ResponseEntity с результатом выполнения запроса.
     */
    public ResponseEntity<Object> createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    /**
     * Получает все запросы пользователя.
     *
     * @param userId Идентификатор пользователя, для которого запрашиваются запросы.
     * @return ResponseEntity с результатами запроса.
     */
    public ResponseEntity<Object> getOwnRequests(long userId) {
        return get("", userId);
    }

    /**
     * Получает все запросы в системе.
     *
     * @param userId Идентификатор пользователя, выполняющего запрос.
     * @return ResponseEntity с результатами запроса.
     */
    public ResponseEntity<Object> getAllRequests(long userId) {
        return get("/all", userId);
    }

    /**
     * Получает запрос по его идентификатору.
     *
     * @param userId    Идентификатор пользователя, запрашивающего информацию.
     * @param requestId Идентификатор запроса, который необходимо получить.
     * @return ResponseEntity с результатами запроса.
     */
    public ResponseEntity<Object> getRequestsById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}