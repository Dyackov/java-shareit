package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Интерфейс UserStorage определяет контракт для операций по работе с пользователями.
 * Он включает методы для создания, обновления, получения, удаления пользователей,
 * а также получения списка всех пользователей.
 * Реализации этого интерфейса могут обеспечивать хранение данных в памяти, базе данных или другом хранилище.
 */
public interface UserStorage {

    /**
     * Создает нового пользователя и сохраняет его в хранилище.
     *
     * @param user объект пользователя, который нужно создать
     * @return созданный пользователь с присвоенным уникальным идентификатором
     */
    User createUser(User user);

    /**
     * Обновляет информацию о существующем пользователе в хранилище.
     *
     * @param user объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    User updateUser(User user);

    /**
     * Возвращает пользователя по его идентификатору.
     * Если пользователь с указанным идентификатором не найден, реализация должна обработать это соответствующим образом.
     *
     * @param userId уникальный идентификатор пользователя
     * @return найденный пользователь
     */
    User getUserById(long userId);

    /**
     * Удаляет пользователя из хранилища по его идентификатору.
     *
     * @param userId уникальный идентификатор пользователя, которого необходимо удалить
     */
    void deleteUserById(long userId);

    /**
     * Возвращает список всех пользователей, хранящихся в хранилище.
     *
     * @return список всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Удаляет всех пользователей из хранилища.
     */
    void deleteAllUsers();
}