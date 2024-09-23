package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;

/**
 * Mapper-класс для преобразования объектов бронирования между различными представлениями (DTO и сущность).
 * <p>
 * Этот класс содержит статические методы для преобразования данных между объектами:
 * - {@link Booking} (сущность бронирования)
 * - {@link BookingDto} (DTO для передачи данных бронирования)
 * - {@link BookingDtoItem} (DTO для представления бронирования без полной информации о предмете и пользователе)
 * </p>
 * <p>
 * Основное назначение этого класса — разделить слои приложения (сущность и DTO),
 * обеспечивая преобразование данных между ними.
 * </p>
 */
public class BookingMapper {

    /**
     * Преобразует сущность {@link Booking} в {@link BookingDto}.
     *
     * @param booking объект {@link Booking}, который необходимо преобразовать
     * @return объект {@link BookingDto} с данными о бронировании
     */
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    /**
     * Преобразует объект {@link BookingDto} обратно в сущность {@link Booking}.
     *
     * @param bookingDto объект {@link BookingDto}, который необходимо преобразовать
     * @return объект {@link Booking}, который будет использоваться в базе данных
     */
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    /**
     * Преобразует сущность {@link Booking} в {@link BookingDtoItem}.
     * Этот метод возвращает упрощённое представление бронирования, содержащее только основную информацию.
     *
     * @param booking объект {@link Booking}, который необходимо преобразовать
     * @return объект {@link BookingDtoItem} с краткой информацией о бронировании
     */
    public static BookingDtoItem toBookingDtoItem(Booking booking) {
        if (booking == null) return null;
        return BookingDtoItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
}
