package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Пользователь сохранён в хранилище.\n{}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён в хранилище.\n{}", user);
        return user;
    }

    @Override
    public User getUserById(long userId) {
        User user = Optional.ofNullable(users.get(userId)).orElseThrow(() -> {
            String errorMessage = "Пользователя с ID: " + userId + " не существует.";
            log.warn("Ошибка получения: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });

        log.info("Получен пользователь из хранилища. ID пользователя: {}.", userId);
        return user;
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
        log.info("Удалён пользователь из хранилища. ID пользователя: {}.", userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получен список всех пользователей из хранилища.");
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        log.info("Удалёны все пользователи из хранилища.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
