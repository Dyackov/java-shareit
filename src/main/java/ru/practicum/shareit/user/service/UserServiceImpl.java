package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link UserService}, предоставляющая основные операции с пользователями.
 * Включает создание, обновление, удаление и получение пользователей, а также валидацию email.
 * Также обрабатывает удаление всех вещей, связанных с пользователем.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage inMemoryUserStorage;
    private final InMemoryItemStorage inMemoryItemStorage;

    /**
     * Создает нового пользователя на основе переданного DTO и сохраняет его в хранилище.
     * Валидирует email на уникальность перед созданием.
     *
     * @param userDto DTO пользователя для создания
     * @return DTO созданного пользователя с присвоенным ID
     * @throws ConflictException если email уже используется другим пользователем
     */
    @Override
    public UserDto createUserDto(UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        User user = inMemoryUserStorage.createUser(UserMapper.toUser(userDto));
        log.info("Создан пользователь DAO: \n{}", user);
        UserDto resultDto = UserMapper.toUserDto(user);
        log.info("Пользователь DTO: \n{}", resultDto);
        return resultDto;
    }

    /**
     * Обновляет данные пользователя на основе переданного DTO.
     * Валидирует email на уникальность перед обновлением.
     *
     * @param userId  ID пользователя, которого нужно обновить
     * @param userDto DTO с обновленными данными пользователя
     * @return DTO обновленного пользователя
     * @throws ConflictException если email уже используется другим пользователем
     */
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

    /**
     * Получает пользователя по его ID и возвращает его в виде DTO.
     *
     * @param userId уникальный идентификатор пользователя
     * @return DTO найденного пользователя
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public UserDto getUserByIdDto(long userId) {
        log.info("Получен пользователь. ID пользователя: {}", userId);
        return UserMapper.toUserDto(inMemoryUserStorage.getUserById(userId));
    }

    /**
     * Удаляет пользователя по его ID из хранилища и все связанные с ним вещи.
     *
     * @param userId уникальный идентификатор пользователя для удаления
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public void deleteUserById(long userId) {
        inMemoryUserStorage.getUserById(userId); // Проверка на существование пользователя
        inMemoryUserStorage.deleteUserById(userId);
        inMemoryItemStorage.deleteAllItemsByUser(userId); // Удаление всех вещей пользователя
        log.info("Удалён пользователь и его вещи. ID пользователя: {}", userId);
    }

    /**
     * Возвращает список всех пользователей в виде DTO.
     *
     * @return список DTO всех пользователей
     */
    @Override
    public List<UserDto> getAllUsersDto() {
        log.info("Получен список всех пользователей.");
        return inMemoryUserStorage.getAllUsers().stream().map(UserMapper::toUserDto).toList();
    }

    /**
     * Удаляет всех пользователей из хранилища и все их вещи.
     */
    @Override
    public void deleteAllUsers() {
        inMemoryUserStorage.deleteAllUsers();
        inMemoryItemStorage.deleteAllItems(); // Удаление всех вещей
        log.info("Удалёны все пользователи и все вещи.");
    }

    /**
     * Валидирует email пользователя на уникальность в хранилище.
     * Если пользователь с таким email уже существует, выбрасывается {@link ConflictException}.
     *
     * @param email email пользователя для проверки
     * @throws ConflictException если пользователь с таким email уже существует
     */
    private void validateUserEmail(String email) {
        if (inMemoryUserStorage.getAllUsers().stream().anyMatch(existingUser -> existingUser.getEmail().equals(email))) {
            log.warn("Ошибка. Пользователь с email: {} уже существует.", email);
            throw new ConflictException("Пользователь с email: " + email + " уже существует.");
        }
    }
}
