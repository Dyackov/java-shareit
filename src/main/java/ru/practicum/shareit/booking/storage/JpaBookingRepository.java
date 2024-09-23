package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования пользователя, отсортированные по времени начала (от последнего к первому).
     *
     * @param userId Идентификатор пользователя.
     * @return Список бронирований.
     */
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    /**
     * Находит все текущие бронирования пользователя на заданный момент времени.
     *
     * @param userId Идентификатор пользователя.
     * @param localDateTime Момент времени для проверки.
     * @return Список текущих бронирований.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = ?1 AND b.start < ?2 AND b.end > ?2
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerStateCurrent(long userId, LocalDateTime localDateTime);

    /**
     * Находит все прошедшие бронирования пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @param localDateTime Момент времени для проверки.
     * @return Список прошедших бронирований.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = ?1 AND b.end < ?2
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerStatePast(long userId, LocalDateTime localDateTime);

    /**
     * Находит все будущие бронирования пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @param localDateTime Момент времени для проверки.
     * @return Список будущих бронирований.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = ?1 AND ?2 < b.start
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByBookerStateFuture(long userId, LocalDateTime localDateTime);

    /**
     * Находит все бронирования пользователя с указанным статусом.
     *
     * @param userId Идентификатор пользователя.
     * @param status Статус бронирования.
     * @return Список бронирований с заданным статусом.
     */
    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status);

    /**
     * Находит все бронирования для владельца вещей.
     *
     * @param userId Идентификатор владельца.
     * @return Список всех бронирований для владельца.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN Item i
            WHERE i.owner.id = ?1
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByOwnerId(long userId);

    /**
     * Находит все текущие бронирования для владельца на заданный момент времени.
     *
     * @param userId Идентификатор владельца.
     * @param localDateTime Момент времени для проверки.
     * @return Список текущих бронирований для владельца.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN Item i
            WHERE i.owner.id = ?1 AND b.start < ?2 AND b.end > ?2
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByOwnerStateCurrent(long userId, LocalDateTime localDateTime);

    /**
     * Находит все прошедшие бронирования для владельца.
     *
     * @param userId Идентификатор владельца.
     * @param localDateTime Момент времени для проверки.
     * @return Список прошедших бронирований для владельца.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN Item i
            WHERE i.owner.id = ?1 AND b.end < ?2
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByOwnerStatePast(long userId, LocalDateTime localDateTime);

    /**
     * Находит все будущие бронирования для владельца.
     *
     * @param userId Идентификатор владельца.
     * @param localDateTime Момент времени для проверки.
     * @return Список будущих бронирований для владельца.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN Item i
            WHERE i.owner.id = ?1 AND ?2 < b.start
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByOwnerStateFuture(long userId, LocalDateTime localDateTime);

    /**
     * Находит все бронирования владельца с указанным статусом.
     *
     * @param userId Идентификатор владельца.
     * @param status Статус бронирования.
     * @return Список бронирований владельца с заданным статусом.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN Item i
            WHERE i.owner.id = ?1 AND b.status = ?2
            """)
    List<Booking> findAllByOwnerIdAndStatus(long userId, BookingStatus status);

    /**
     * Находит последнее бронирование для вещи по идентификатору.
     *
     * @param itemId Идентификатор вещи.
     * @param localDateTime Момент времени для проверки.
     * @return Последнее бронирование, если есть.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = ?1 AND b.end < ?2
            ORDER BY b.end DESC
            LIMIT 1
            """)
    Optional<Booking> findLastBookingByBookerId(long itemId, LocalDateTime localDateTime);

    /**
     * Находит следующее бронирование для вещи по идентификатору.
     *
     * @param itemId Идентификатор вещи.
     * @param localDateTime Момент времени для проверки.
     * @return Следующее бронирование, если есть.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = ?1 AND b.start > ?2
            ORDER BY b.start ASC
            LIMIT 1
            """)
    Optional<Booking> findNextBookingByBookerId(long itemId, LocalDateTime localDateTime);

    /**
     * Проверяет авторизацию на отзыв о вещи после аренды.
     *
     * @param bookerId Идентификатор арендатора.
     * @param itemId Идентификатор вещи.
     * @param localDateTime Момент времени для проверки.
     * @return Бронирование, если есть, соответствующее критериям.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.end < ?3
            """)
    Optional<Booking> checkItemReviewAuthorizationAfterRental(long bookerId, long itemId, LocalDateTime localDateTime);
}
