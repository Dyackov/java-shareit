package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage inMemoryUserStorage;

    @Override
    public UserDto createUserDto(UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        User user = inMemoryUserStorage.createUser(UserMapper.toUser(userDto));
        log.info("Создан пользователь DAO: \n{}", user);
        UserDto resultDto = UserMapper.toUserDto(user);
        log.info("Пользователь DTO: \n{}", resultDto);
        return resultDto;
    }

    @Override
    public UserDto updateUserDto(long userId, UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        User user = inMemoryUserStorage.getUserById(userId);
        log.info("Старый пользователь DAO: \n{}", user);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        log.info("Обновлённый пользователь DAO: \n{}", user);
        UserDto resultDto = UserMapper.toUserDto(inMemoryUserStorage.updateUser(user));
        log.info("Обновлённый пользователь DTO: \n{}", resultDto);
        return resultDto;
    }

    @Override
    public UserDto getUserByIdDto(long userId) {
        log.info("Получен пользователь. ID пользователя: {}", userId);
        return UserMapper.toUserDto(inMemoryUserStorage.getUserById(userId));
    }

    @Override
    public void deleteUserById(long userId) {
        inMemoryUserStorage.getUserById(userId);
        inMemoryUserStorage.deleteUserById(userId);
        log.info("Удалён пользователь. ID пользователя: {}", userId);
    }

    @Override
    public List<UserDto> getAllUsersDto() {
        log.info("Получен список всех пользователей.");
        return inMemoryUserStorage.getAllUsers().stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public void deleteAllUsers() {
        inMemoryUserStorage.deleteAllUsers();
        log.info("Удалёны все пользователи.");
    }

    private void validateUserEmail(String email) {
        if (inMemoryUserStorage.getAllUsers().stream().anyMatch(existingUser -> existingUser.getEmail().equals(email))) {
            log.warn("Ошибка. Пользователь с email: {} уже существует.", email);
            throw new ConflictException("Пользователь с email: " + email + " уже существует.");
        }
    }
}

