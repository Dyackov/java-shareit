package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link ItemRequest}.
 * Обеспечивает доступ к данным запросов на предметы и
 * предоставляет методы для выполнения операций CRUD.
 */
public interface JpaItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Находит все запросы на предметы, созданные конкретным пользователем,
     * отсортированные по дате создания в порядке убывания.
     *
     * @param requestorId идентификатор пользователя, создавшего запросы
     * @return список запросов на предметы, созданных пользователем
     */
    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    /**
     * Находит все запросы на предметы, кроме тех, что были созданы конкретным пользователем,
     * отсортированные по дате создания в порядке убывания.
     *
     * @param requestorId идентификатор пользователя, исключенного из результатов
     * @return список запросов на предметы, созданных другими пользователями
     */
    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}