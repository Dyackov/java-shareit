package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

/**
 * Интерфейс JpaUserRepository представляет собой репозиторий для работы с сущностью {@link User}.
 * <p>
 * Он расширяет JpaRepository, предоставляя стандартные методы для работы с пользователями, такие как
 * сохранение, удаление и поиск пользователей в базе данных.
 * </p>
 */
public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя для поиска
     * @return Optional<User> - найденный пользователь, если существует, иначе пустой Optional
     */
    @Query("""
SELECT u FROM User u
WHERE u.email = ?1
""")
    Optional<User> findByEmail(String email);
}
