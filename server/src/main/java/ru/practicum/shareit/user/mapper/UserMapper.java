package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Класс UserMapper предназначен для преобразования между сущностью User и объектом DTO (UserDto).
 * <p>
 * Этот класс предоставляет статические методы для конвертации User в UserDto и обратно.
 */
public class UserMapper {

    /**
     * Преобразует объект класса User в объект класса UserDto.
     *
     * @param user объект класса User, который нужно преобразовать в UserDto
     * @return объект класса UserDto, содержащий данные пользователя
     */
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Преобразует объект класса UserDto в объект класса User.
     *
     * @param userDto объект класса UserDto, который нужно преобразовать в User
     * @return объект класса User, содержащий данные пользователя
     */
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}