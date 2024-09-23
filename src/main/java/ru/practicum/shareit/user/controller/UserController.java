package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * Класс UserController отвечает за обработку HTTP-запросов, связанных с пользователями.
 * <p>
 * Этот контроллер предоставляет API для создания, обновления, получения и удаления пользователей.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService jpaUserService;

    /**
     * Обрабатывает POST-запрос для создания нового пользователя.
     *
     * @param userDto DTO объекта пользователя, содержащий данные для создания.
     * @return созданный пользователь в формате UserDto.
     */
    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос на создание пользователя: \n{}", userDto);
        return jpaUserService.createUserDto(userDto);
    }

    /**
     * Обрабатывает PATCH-запрос для обновления данных пользователя.
     *
     * @param userId  ID пользователя, которого нужно обновить.
     * @param userDto DTO с новыми данными для обновления пользователя.
     * @return обновлённый пользователь в формате UserDto.
     */
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя. ID пользователя: {}.\nНовые данные: {}", userId, userDto);
        return jpaUserService.updateUserDto(userId, userDto);
    }

    /**
     * Обрабатывает GET-запрос для получения данных пользователя по его ID.
     *
     * @param userId ID пользователя, данные которого нужно получить.
     * @return пользователь в формате UserDto.
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Запрос на получение пользователя. ID пользователя: {}", userId);
        return jpaUserService.getUserByIdDto(userId);
    }

    /**
     * Обрабатывает DELETE-запрос для удаления пользователя по его ID.
     *
     * @param userId ID пользователя, которого нужно удалить.
     */
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("Запрос на удаление пользователя. ID пользователя: {}", userId);
        jpaUserService.deleteUserById(userId);
    }

    /**
     * Обрабатывает GET-запрос для получения списка всех пользователей.
     *
     * @return список всех пользователей в формате List<UserDto>.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей.");
        return jpaUserService.getAllUsersDto();
    }

    /**
     * Обрабатывает DELETE-запрос для удаления всех пользователей.
     */
    @DeleteMapping()
    public void deleteAllUsers() {
        log.info("Запрос на удаление всех пользователей.");
        jpaUserService.deleteAllUsers();
    }

}
