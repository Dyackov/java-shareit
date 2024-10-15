package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserClient;

/**
 * Контроллер для управления пользователями в системе.
 * <p>
 * Этот класс предоставляет REST API для создания, обновления, получения и удаления пользователей.
 * Он обрабатывает HTTP-запросы и взаимодействует с сервисом пользователей.
 * </p>
 */
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto DTO пользователя, содержащий данные для создания нового пользователя.
     * @return ResponseEntity с результатом выполнения запроса на создание пользователя.
     */
    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос на создание пользователя: \n{}", userDto);
        ResponseEntity<Object> result = userClient.createUserDto(userDto);
        log.info("Создан пользователь: \n{}", result);
        return result;
    }

    /**
     * Обновляет данные пользователя по его идентификатору.
     *
     * @param userId  Идентификатор пользователя, данные которого необходимо обновить.
     * @param userDto DTO пользователя с новыми данными.
     * @return ResponseEntity с результатом выполнения запроса на обновление пользователя.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя. ID пользователя: {}.\nНовые данные: {}", userId, userDto);
        return userClient.updateUserDto(userId, userDto);
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя, информацию о котором необходимо получить.
     * @return ResponseEntity с данными о пользователе.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Запрос на получение пользователя. ID пользователя: {}", userId);
        return userClient.getUserByIdDto(userId);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя, которого необходимо удалить.
     * @return ResponseEntity с результатом выполнения запроса на удаление пользователя.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Запрос на удаление пользователя. ID пользователя: {}", userId);
        return userClient.deleteUserById(userId);
    }

    /**
     * Получает список всех пользователей в системе.
     *
     * @return ResponseEntity с данными о всех пользователях.
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей.");
        return userClient.getAllUsersDto();
    }

    /**
     * Удаляет всех пользователей из системы.
     *
     * @return ResponseEntity с результатом выполнения запроса на удаление всех пользователей.
     */
    @DeleteMapping()
    public ResponseEntity<Object> deleteAllUsers() {
        log.info("Запрос на удаление всех пользователей.");
        return userClient.deleteAllUsers();
    }
}