package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс User представляет модель пользователя.
 * Используется для хранения информации о пользователе, такой как уникальный идентификатор, имя и email.
 * <p>
 * Аннотации Lombok:
 * <p>- @Data: генерирует геттеры, сеттеры, методы equals(), hashCode() и toString().
 * <p>- @Builder: предоставляет удобный способ создания экземпляров класса через паттерн "строитель" (Builder).
 */
@Data
@Builder
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Email пользователя. Предполагается, что email является уникальным для каждого пользователя.
     */
    private String email;

}

