package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Класс, представляющий модель бронирования.
 * <p>
 * Этот класс используется для хранения информации о бронировании вещи.
 * Он содержит данные о времени начала и окончания бронирования,
 * а также информацию о пользователе, который сделал бронирование,
 * и о самой вещи, которую бронируют.
 * </p>
 */
@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    /**
     * Уникальный идентификатор бронирования.
     */
    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Время начала бронирования.
     */
    @Column(name = "start_date")
    private LocalDateTime start;

    /**
     * Время окончания бронирования.
     */
    @Column(name = "end_date")
    private LocalDateTime end;

    /**
     * Вещь, которую бронируют.
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Пользователь, который сделал бронирование.
     */
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    /**
     * Статус бронирования.
     * <p>
     * Статус может принимать значения, указанные в перечислении {@link BookingStatus}.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
