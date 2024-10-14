package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Сервисный класс для работы с пользователями через REST API.
 * <p>
 * Этот класс предоставляет методы для выполнения CRUD операций с пользователями.
 * Он использует {@link BaseClient} для отправки HTTP запросов на сервер.
 * </p>
 */
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    /**
     * Конструктор для инициализации UserClient с заданным URL сервера и RestTemplate.
     *
     * @param serverUrl URL сервера, на который будут отправляться запросы.
     * @param builder   Строитель для создания экземпляра RestTemplate.
     */
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto DTO пользователя, содержащий данные для создания нового пользователя.
     * @return ResponseEntity с результатом выполнения запроса на создание пользователя.
     */
    public ResponseEntity<Object> createUserDto(UserDto userDto) {
        return post("", userDto);
    }

    /**
     * Обновляет данные пользователя по его идентификатору.
     *
     * @param userId  Идентификатор пользователя, данные которого необходимо обновить.
     * @param userDto DTO пользователя с новыми данными.
     * @return ResponseEntity с результатом выполнения запроса на обновление пользователя.
     */
    public ResponseEntity<Object> updateUserDto(long userId, UserDto userDto) {
        return patch("/" + userId, userDto);
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя, информацию о котором необходимо получить.
     * @return ResponseEntity с данными о пользователе.
     */
    public ResponseEntity<Object> getUserByIdDto(long userId) {
        return get("/" + userId);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя, которого необходимо удалить.
     * @return ResponseEntity с результатом выполнения запроса на удаление пользователя.
     */
    public ResponseEntity<Object> deleteUserById(long userId) {
        return delete("/" + userId);
    }

    /**
     * Получает список всех пользователей в системе.
     *
     * @return ResponseEntity с данными о всех пользователях.
     */
    public ResponseEntity<Object> getAllUsersDto() {
        return get("");
    }

    /**
     * Удаляет всех пользователей из системы.
     *
     * @return ResponseEntity с результатом выполнения запроса на удаление всех пользователей.
     */
    public ResponseEntity<Object> deleteAllUsers() {
        return delete("");
    }
}