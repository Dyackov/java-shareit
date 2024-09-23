package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с комментариями.
 * Этот интерфейс предоставляет методы для взаимодействия с базой данных
 * и управления комментариями.
 */
public interface JpaCommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Находит список комментариев по идентификатору вещи.
     *
     * @param itemId идентификатор вещи, для которой нужно найти комментарии
     * @return список комментариев, обернутый в Optional. Если комментарии не найдены,
     *         возвращается пустой Optional.
     */
    @Query("""
    SELECT c FROM Comment c
    WHERE c.item.id = ?1
    """)
    Optional<List<Comment>> findCommentsByItemId(long itemId);
}
