package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность комментария.
 * Содержит информацию о комментарии к вещи, его авторе и времени создания.
 */
@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    /**
     * Уникальный идентификатор комментария.
     */
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст комментария.
     */
    private String text;

    /**
     * Вещь, к которой принадлежит комментарий.
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Автор комментария.
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    /**
     * Время создания комментария.
     */
    private LocalDateTime created;

}