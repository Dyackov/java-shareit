package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUserDto(UserDto userDto);

    UserDto updateUserDto(long userId, UserDto userDto);

    UserDto getUserByIdDto(long userId);

    void deleteUserById(long userId);

    List<UserDto> getAllUsersDto();

    void deleteAllUsers();

}
