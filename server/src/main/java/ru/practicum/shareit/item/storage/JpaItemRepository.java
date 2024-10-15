package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Репозиторий для работы с вещами.
 * Этот интерфейс предоставляет методы для взаимодействия с базой данных
 * и управления объектами типа Item.
 */
public interface JpaItemRepository extends JpaRepository<Item, Long> {

    /**
     * Находит все вещи, принадлежащие пользователю по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи вещи нужно найти
     * @return список вещей, принадлежащих пользователю
     */
    List<Item> findAllByOwnerId(long userId);

    /**
     * Находит доступные вещи по имени или описанию.
     * Вещи должны быть доступны (available = true) и содержать указанный текст
     * в названии или описании (независимо от регистра).
     *
     * @param name        текст для поиска в названии вещи
     * @param description текст для поиска в описании вещи
     * @return список доступных вещей, соответствующих критериям поиска
     */
    List<Item> findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(
            String name, String description);

    /**
     * Удаляет все вещи, принадлежащие пользователю по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи вещи нужно удалить
     */
    void deleteAllByOwnerId(long userId);

    /**
     * Находит вещи по идентификатору запроса.
     *
     * @param requestId идентификатор запроса
     * @return список вещей, соответствующих указанному запросу
     */
    @Query("""
            SELECT i FROM Item i
            WHERE i.request.id = ?1
            """)
    List<Item> findItemsByRequestId(long requestId);

    /**
     * Находит вещи по списку идентификаторов запросов.
     *
     * @param requestIds список идентификаторов запросов
     * @return список вещей, соответствующих указанным запросам
     */
    @Query("""
            SELECT i FROM Item i
            WHERE i.request.id IN :requestIds
            """)
    List<Item> findAllByRequestIds(@Param("requestIds") List<Long> requestIds);
}