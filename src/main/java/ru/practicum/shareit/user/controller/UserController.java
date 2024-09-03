package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userServiceImpl;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос на создание пользователя: \n{}", userDto);
        return userServiceImpl.createUserDto(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя. ID пользователя: {}.\nНовые данные: {}", userId, userDto);
        return userServiceImpl.updateUserDto(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Запрос на получение пользователя. ID пользователя: {}", userId);
        return userServiceImpl.getUserByIdDto(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("Запрос на удаление пользователя. ID пользователя: {}", userId);
        userServiceImpl.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей.");
        return userServiceImpl.getAllUsersDto();
    }

    @DeleteMapping()
    public void deleteAllUsers() {
        log.info("Запрос на удаление всех пользователей.");
        userServiceImpl.deleteAllUsers();
    }

}
