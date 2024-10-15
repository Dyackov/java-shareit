package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность для представления запроса на предмет.
 * Хранит информацию о запросе, включая его идентификатор,
 * описание, запрашивающего пользователя и дату создания.
 */
@Entity
@Table(name = "item_requests", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    /**
     * Уникальный идентификатор запроса.
     */
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Описание запроса.
     */
    @Column(nullable = false)
    private String description;

    /**
     * Пользователь, создавший запрос.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor")
    private User requestor;

    /**
     * Дата и время создания запроса.
     */
    private LocalDateTime created;

    /**
     * Определяет, равен ли текущий объект другому объекту.
     *
     * @param o объект для сравнения
     * @return true, если объекты равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest that = (ItemRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(requestor, that.requestor) &&
                Objects.equals(created, that.created);
    }

    /**
     * Генерирует хэш-код для объекта.
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor, created);
    }
}