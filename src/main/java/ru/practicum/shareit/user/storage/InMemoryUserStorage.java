package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

/**
 * Класс InMemoryUserStorage реализует интерфейс UserStorage и предоставляет
 * in-memory (в памяти) хранилище для пользователей.
 * Основное назначение этого класса — управлять пользователями, их созданием,
 * обновлением, удалением и извлечением информации, без использования
 * базы данных.
 */
@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    /**
     * Создает нового пользователя и сохраняет его в памяти.
     * Назначает уникальный идентификатор пользователю.
     *
     * @param user объект пользователя, который нужно сохранить
     * @return сохраненный пользователь с назначенным ID
     */
    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Пользователь сохранён в хранилище.\n{}", user);
        return user;
    }

    /**
     * Обновляет информацию о существующем пользователе в памяти.
     *
     * @param user обновленный объект пользователя
     * @return обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён в хранилище.\n{}", user);
        return user;
    }

    /**
     * Возвращает пользователя по его идентификатору.
     * Если пользователь с указанным ID не найден, выбрасывается исключение NotFoundException.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public User getUserById(long userId) {
        User user = Optional.ofNullable(users.get(userId)).orElseThrow(() -> {
            String errorMessage = "Пользователя с ID: " + userId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });
        log.debug("Получен пользователь из хранилища. ID пользователя: {}.", userId);
        return user;
    }

    /**
     * Удаляет пользователя из памяти по его идентификатору.
     *
     * @param userId идентификатор пользователя, которого нужно удалить
     */
    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
        log.debug("Удалён пользователь из хранилища. ID пользователя: {}.", userId);
    }

    /**
     * Возвращает список всех пользователей, хранящихся в памяти.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> getAllUsers() {
        log.debug("Получен список всех пользователей из хранилища.");
        return new ArrayList<>(users.values());
    }

    /**
     * Удаляет всех пользователей из памяти.
     */
    @Override
    public void deleteAllUsers() {
        users.clear();
        log.debug("Удалёны все пользователи из хранилища.");
    }

    /**
     * Генерирует следующий уникальный идентификатор для пользователя.
     * Идентификатор представляет собой максимальный ID среди существующих пользователей, увеличенный на 1.
     *
     * @return следующий уникальный идентификатор
     */
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
