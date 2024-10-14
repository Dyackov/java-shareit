package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class BookingTest {

    @Test
    void testEquals_SameObject() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        assertThat(booking.equals(booking), is(true));
    }

    @Test
    void testEquals_Null() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        assertThat(booking.equals(null), is(false));
    }

    @Test
    void testEquals_DifferentClass() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        assertThat(booking.equals("Not a Booking"), is(false));
    }

    @Test
    void testEquals_SameFields() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .status(booking1.getStatus())
                .build();

        assertThat(booking1.equals(booking2), is(true));
    }

    @Test
    void testEquals_DifferentFields() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        assertThat(booking1.equals(booking2), is(false));
    }

    @Test
    void testHashCode_SameFields() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .status(booking1.getStatus())
                .build();

        assertThat(booking1.hashCode(), is(booking2.hashCode()));
    }

    @Test
    void testHashCode_DifferentId() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .status(booking1.getStatus())
                .build();

        assertThat(booking1.hashCode(), is(not(booking2.hashCode())));
    }

    @Test
    void testHashCode_DifferentStart() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1)) // Разное время начала
                .end(booking1.getEnd())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .status(booking1.getStatus())
                .build();

        assertThat(booking1.hashCode(), is(not(booking2.hashCode())));
    }

    @Test
    void testHashCode_DifferentEnd() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(LocalDateTime.now().plusDays(2))
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .status(booking1.getStatus())
                .build();

        assertThat(booking1.hashCode(), is(not(booking2.hashCode())));
    }

    @Test
    void testHashCode_DifferentStatus() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .status(BookingStatus.REJECTED)
                .build();

        assertThat(booking1.hashCode(), is(not(booking2.hashCode())));
    }
}