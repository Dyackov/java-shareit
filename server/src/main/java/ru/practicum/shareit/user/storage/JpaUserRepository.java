package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Интерфейс JpaUserRepository представляет собой репозиторий для работы с сущностью {@link User}.
 * <p>
 * Он расширяет JpaRepository, предоставляя стандартные методы для работы с пользователями, такие как
 * сохранение, удаление и поиск пользователей в базе данных.
 * </p>
 */
public interface JpaUserRepository extends JpaRepository<User, Long> {}
