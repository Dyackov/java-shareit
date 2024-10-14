package ru.practicum.shareit.booking.model.enums;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.exception.UnsupportedStateException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingStateTest {

    @Test
    void testFromString_ValidStateAll() {
        BookingState state = BookingState.fromString("ALL");
        assertThat(state, is(BookingState.ALL));
    }

    @Test
    void testFromString_ValidStateCurrent() {
        BookingState state = BookingState.fromString("CURRENT");
        assertThat(state, is(BookingState.CURRENT));
    }

    @Test
    void testFromString_ValidStatePast() {
        BookingState state = BookingState.fromString("PAST");
        assertThat(state, is(BookingState.PAST));
    }

    @Test
    void testFromString_ValidStateFuture() {
        BookingState state = BookingState.fromString("FUTURE");
        assertThat(state, is(BookingState.FUTURE));
    }

    @Test
    void testFromString_ValidStateWaiting() {
        BookingState state = BookingState.fromString("WAITING");
        assertThat(state, is(BookingState.WAITING));
    }

    @Test
    void testFromString_ValidStateRejected() {
        BookingState state = BookingState.fromString("REJECTED");
        assertThat(state, is(BookingState.REJECTED));
    }

    @Test
    void testFromString_ValidStateWithLowerCase() {
        BookingState state = BookingState.fromString("all");
        assertThat(state, is(BookingState.ALL));
    }

    @Test
    void testFromString_InvalidState() {
        UnsupportedStateException exception = assertThrows(UnsupportedStateException.class, () -> {
            BookingState.fromString("INVALID_STATE");
        });

        assertThat(exception.getMessage(), startsWith("Unknown state: "));
    }
}
