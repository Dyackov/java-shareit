package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с пользователями.
 * <p>
 * Содержит методы для создания, обновления, удаления и получения информации о пользователях.
 * Также включает валидацию email на уникальность.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JpaUserRepository jpaUserRepository;
    private final JpaItemRepository jpaItemRepository;

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
    @Override
    public UserDto createUserDto(UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        User user = jpaUserRepository.save(UserMapper.toUser(userDto));
        log.info("Создан пользователь DAO: \n{}", user);
        UserDto resultDto = UserMapper.toUserDto(user);
        log.info("Пользователь DTO: \n{}", resultDto);
        return resultDto;
    }

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
    @Override
    public UserDto updateUserDto(long userId, UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        User user = checkUserExist(userId);
        log.info("Старый пользователь DAO: \n{}", user);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        log.info("Обновлённый пользователь DAO: \n{}", user);
        UserDto resultDto = UserMapper.toUserDto(jpaUserRepository.save(user));
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
        User user = checkUserExist(userId);
        log.info("Получен пользователь. ID пользователя: {}.", userId);
        return UserMapper.toUserDto(user);
    }

    /**
     * Удаляет пользователя по его ID.
     * <p>
     * Также удаляет все вещи, принадлежащие этому пользователю.
     * </p>
     *
     * @param userId уникальный идентификатор пользователя для удаления
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public void deleteUserById(long userId) {
        checkUserExist(userId);
        jpaUserRepository.deleteById(userId);
        jpaItemRepository.deleteAllByOwnerId(userId);
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
        return jpaUserRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    /**
     * Удаляет всех пользователей из хранилища и все их вещи.
     */
    @Override
    public void deleteAllUsers() {
        jpaUserRepository.deleteAll();
        jpaItemRepository.deleteAll();
        log.info("Удалены все пользователи и все вещи.");
    }

    /**
     * Валидирует email пользователя на уникальность в хранилище.
     * <p>
     * Если пользователь с таким email уже существует, выбрасывается {@link ConflictException}.
     * </p>
     *
     * @param email email пользователя для проверки
     * @throws ConflictException если пользователь с таким email уже существует
     */
    private void validateUserEmail(String email) {
        if (jpaUserRepository.findByEmail(email).isPresent()) {
            log.warn("Ошибка. Пользователь с email: {} уже существует.", email);
            throw new ConflictException("Пользователь с email: " + email + " уже существует.");
        }
    }

    /**
     * Проверяет существование пользователя по его ID.
     *
     * @param userId уникальный идентификатор пользователя
     * @return объект User, если пользователь найден
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public User checkUserExist(long userId) {
        return jpaUserRepository.findById(userId).orElseThrow(() -> {
            String errorMessage = "Пользователя с ID: " + userId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });
    }
}
