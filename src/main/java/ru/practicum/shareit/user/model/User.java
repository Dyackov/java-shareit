package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс User представляет модель пользователя в системе.
 * <p>
 * Данный класс используется для хранения информации о пользователе, включая уникальный идентификатор,
 * имя и адрес электронной почты. Все поля имеют соответствующие ограничения для валидации.
 * </p>
 * <p>
 * Аннотации Lombok:
 * <ul>
 *     <li><b>@Data</b>: генерирует геттеры, сеттеры, а также методы equals(), hashCode() и toString().</li>
 *     <li><b>@Builder</b>: предоставляет удобный способ создания экземпляров класса через паттерн "строитель" (Builder).</li>
 *     <li><b>@NoArgsConstructor</b>: генерирует конструктор без параметров.</li>
 *     <li><b>@AllArgsConstructor</b>: генерирует конструктор со всеми параметрами.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "users", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     * Не может быть пустым.
     */
    @Column(nullable = false)
    @NotNull(message = "Имя не может быть пустым.")
    private String name;

    /**
     * Email пользователя.
     * Предполагается, что email является уникальным для каждого пользователя.
     * Не может быть пустым и должен содержать символ "@".
     */
    @Column(unique = true)
    @NotNull(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна содержать символ @.")
    private String email;
}

