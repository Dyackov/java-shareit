package ru.practicum.shareit.user.service;

import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Интерфейс UserService предоставляет основные операции для работы с пользователями.
 * <p>
 * Методы включают создание, обновление, удаление, получение пользователя, а также
 * получение списка всех пользователей и удаление всех пользователей.
 * </p>
 */
public interface UserService {

    /**
     * Создает нового пользователя на основе переданного DTO и возвращает созданного пользователя в виде DTO.
     * <p>
     * Валидирует email на уникальность перед созданием.
     * </p>
     *
     * @param userDto DTO пользователя для создания
     * @return DTO созданного пользователя с присвоенным ID
     * @throws ConflictException если email уже используется другим пользователем
     */
    UserDto createUserDto(UserDto userDto);

    /**
     * Обновляет данные пользователя на основе переданного DTO.
     * <p>
     * Валидирует email на уникальность перед обновлением.
     * </p>
     *
     * @param userId  ID пользователя, которого нужно обновить
     * @param userDto DTO с обновленными данными пользователя
     * @return DTO обновленного пользователя
     * @throws ConflictException если email уже используется другим пользователем
     */
    UserDto updateUserDto(long userId, UserDto userDto);

    /**
     * Получает пользователя по его ID и возвращает его в виде DTO.
     *
     * @param userId уникальный идентификатор пользователя
     * @return DTO найденного пользователя
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    UserDto getUserByIdDto(long userId);

    /**
     * Удаляет пользователя по его ID.
     *
     * @param userId уникальный идентификатор пользователя для удаления
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    void deleteUserById(long userId);

    /**
     * Возвращает список всех пользователей в виде DTO.
     *
     * @return список DTO всех пользователей
     */
    List<UserDto> getAllUsersDto();

    /**
     * Удаляет всех пользователей из системы.
     */
    void deleteAllUsers();

    /**
     * Проверяет существование пользователя по его ID.
     *
     * @param userId уникальный идентификатор пользователя
     * @return объект User, если пользователь найден
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    User checkUserExist(long userId);
}
