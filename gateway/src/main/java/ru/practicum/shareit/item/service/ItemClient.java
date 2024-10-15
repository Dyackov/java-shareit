package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

/**
 * Класс-сервис для управления запросами к API вещей.
 * <p>
 * Этот сервис отвечает за выполнение операций с вещами через REST API,
 * включая создание, обновление, получение, удаление вещей и работу с комментариями.
 * </p>
 */
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    /**
     * Конструктор для инициализации клиента с заданным URL сервера.
     *
     * @param serverUrl URL сервера.
     * @param builder   объект для настройки RestTemplate.
     */
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Создаёт новую вещь.
     *
     * @param userId  идентификатор пользователя, создающего вещь.
     * @param itemDto объект, содержащий данные о создаваемой вещи.
     * @return объект ResponseEntity с результатом операции.
     */
    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    /**
     * Обновляет информацию о существующей вещи.
     *
     * @param userId  идентификатор пользователя, обновляющего вещь.
     * @param itemId  идентификатор обновляемой вещи.
     * @param itemDto объект, содержащий новые данные о вещи.
     * @return объект ResponseEntity с результатом операции.
     */
    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        return patch("" + userId, itemId, itemDto);
    }

    /**
     * Получает информацию о вещи по её идентификатору.
     *
     * @param userId идентификатор пользователя, запрашивающего информацию.
     * @param itemId идентификатор запрашиваемой вещи.
     * @return объект ResponseEntity с данными о вещи.
     */
    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    /**
     * Получает список всех вещей текущего пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return объект ResponseEntity со списком вещей.
     */
    public ResponseEntity<Object> getAllItemsFromUser(long userId) {
        return get("" + userId);
    }

    /**
     * Ищет доступные для аренды вещи по заданному тексту.
     *
     * @param userId идентификатор пользователя.
     * @param text   текст для поиска.
     * @return объект ResponseEntity со списком найденных вещей.
     */
    public ResponseEntity<Object> searchAvailableItemsByText(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("", userId, parameters);
    }

    /**
     * Удаляет вещь по её идентификатору.
     *
     * @param userId идентификатор пользователя, удаляющего вещь.
     * @param itemId идентификатор удаляемой вещи.
     * @return объект ResponseEntity с результатом операции.
     */
    public ResponseEntity<Object> deleteItemById(long userId, long itemId) {
        return delete("" + itemId, userId);
    }

    /**
     * Удаляет все вещи текущего пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return объект ResponseEntity с результатом операции.
     */
    public ResponseEntity<Object> deleteAllItemsByUser(long userId) {
        return delete("", userId);
    }

    /**
     * Создаёт комментарий к вещи.
     *
     * @param authorId          идентификатор пользователя, оставляющего комментарий.
     * @param itemId            идентификатор вещи, к которой оставляется комментарий.
     * @param commentDtoRequest объект, содержащий данные комментария.
     * @return объект ResponseEntity с результатом операции.
     */
    public ResponseEntity<Object> createComment(long authorId, long itemId, CommentDtoRequest commentDtoRequest) {
        return post("/" + itemId + "/comment", authorId, commentDtoRequest);
    }
}