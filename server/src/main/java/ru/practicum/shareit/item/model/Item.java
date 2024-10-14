package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

/**
 * Представляет собой модель данных для вещи.
 * Содержит информацию о вещи, включая её идентификатор, название, описание, доступность,
 * владельца и запрос, с которым вещь может быть связана.
 */
@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    /**
     * Уникальный идентификатор вещи.
     * Устанавливается системой при создании вещи.
     */
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название вещи.
     * Например, "Сверло" или "Книга по Java".
     */
    @Column(nullable = false)
    private String name;

    /**
     * Описание вещи.
     * Дополнительная информация о вещи, такая как её состояние или особенности.
     */
    private String description;

    /**
     * Доступность вещи.
     * Если значение истинно, вещь доступна для аренды; если ложно, вещь недоступна.
     */
    private Boolean available;

    /**
     * Идентификатор владельца вещи.
     * Указывает на пользователя, который является владельцем этой вещи.
     */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id)
                && Objects.equals(name, item.name)
                && Objects.equals(description, item.description)
                && Objects.equals(available, item.available)
                && Objects.equals(owner, item.owner)
                && Objects.equals(request, item.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, request);
    }
}