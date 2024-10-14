package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class BookingServiceImplTest {

    @Mock
    private JpaBookingRepository jpaBookingRepository;
    @Mock
    private UserService userServiceImpl;
    @Mock
    private ItemServiceImpl itemServiceImpl;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    private User user;
    private Item item;
    private BookingDtoRequest bookingDtoRequest;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        item = Item.builder().id(1L).available(true).owner(user).build();
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void createBooking_ShouldReturnBookingDto_WhenBookingIsValid() {
        User owner = User.builder().id(2L).name("Owner").email("owner@example.com").build();
        User user = User.builder().id(1L).name("User").email("user@example.com").build();
        Item item = Item.builder().id(1L).name("Item").owner(owner).available(true).build();

        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(itemServiceImpl.checkItemExist(1L)).thenReturn(item);
        when(jpaBookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingServiceImpl.createBooking(1L, bookingDtoRequest);

        assertEquals(bookingDto, result);
        verify(userServiceImpl).checkUserExist(1L);
        verify(itemServiceImpl).checkItemExist(1L);
        verify(jpaBookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowNotFoundException_WhenUserIsOwnerOfItem() {
        item.getOwner().setId(1L);

        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(itemServiceImpl.checkItemExist(1L)).thenReturn(item);

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.createBooking(1L, bookingDtoRequest));
    }

    @Test
    void createBooking_ShouldThrowValidationException_WhenItemIsNotAvailable() {
        item.setAvailable(false);

        User owner = User.builder().id(2L).name("Owner").email("owner@example.com").build();
        User user = User.builder().id(1L).name("User").email("user@example.com").build();

        item.setOwner(owner);

        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(itemServiceImpl.checkItemExist(1L)).thenReturn(item);

        assertThrows(ValidationException.class, () -> bookingServiceImpl.createBooking(1L, bookingDtoRequest));
    }

    @Test
    void confirmOrRejectBooking_ShouldApprove_WhenApprovedIsTrue() {
        when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(jpaBookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingServiceImpl.confirmOrRejectBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(jpaBookingRepository).save(any(Booking.class));
    }

    @Test
    void confirmOrRejectBooking_ShouldReject_WhenApprovedIsFalse() {
        when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(jpaBookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingServiceImpl.confirmOrRejectBooking(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(jpaBookingRepository).save(any(Booking.class));
    }

    @Test
    void confirmOrRejectBooking_ShouldThrowForbiddenException_WhenUserIsNotOwner() {
        User anotherUser = User.builder().id(2L).build();
        item.setOwner(anotherUser);

        when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingServiceImpl
                .confirmOrRejectBooking(1L, 1L, true));
    }

    @Test
    void confirmOrRejectBooking_ShouldThrowValidationException_WhenBookingIsAlreadyConfirmed() {
        booking.setStatus(BookingStatus.APPROVED);

        when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingServiceImpl
                .confirmOrRejectBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_ShouldReturnBookingDto_WhenUserIsAuthorized() {
        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingServiceImpl.getBookingById(1L, 1L);

        assertEquals(bookingDto, result);
        verify(jpaBookingRepository).findById(1L);
    }

    @Test
    void getBookingState_ShouldReturnListOfBookings_WhenStateIsAll() {
        List<Booking> bookings = List.of(booking);

        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaBookingRepository.findAllByBookerIdOrderByStartDesc(1L)).thenReturn(bookings);

        List<BookingDto> result = bookingServiceImpl.getBookingState(1L, BookingState.ALL);

        assertEquals(1, result.size());
        verify(jpaBookingRepository).findAllByBookerIdOrderByStartDesc(1L);
    }

    @Test
    void getBookingState_ShouldReturnEmptyList_WhenStateIsCurrent() {
        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaBookingRepository.findAllByBookerStateCurrent(anyLong(), any())).thenReturn(List.of());

        List<BookingDto> result = bookingServiceImpl.getBookingState(1L, BookingState.CURRENT);

        assertTrue(result.isEmpty());

        ArgumentCaptor<LocalDateTime> dateTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(jpaBookingRepository).findAllByBookerStateCurrent(eq(1L), dateTimeCaptor.capture());

        LocalDateTime capturedDateTime = dateTimeCaptor.getValue();
        LocalDateTime now = LocalDateTime.now();

        long difference = Math.abs(now.toInstant(ZoneOffset.UTC).toEpochMilli() - capturedDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        assertTrue(difference < 100);
    }

    @Test
    void getAllByOwnerId_ShouldReturnListOfBookings_WhenStateIsAll() {
        List<Booking> bookings = List.of(booking);

        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaBookingRepository.findAllByOwnerId(1L)).thenReturn(bookings);

        List<BookingDto> result = bookingServiceImpl.getAllByOwnerId(1L, BookingState.ALL);

        assertEquals(1, result.size());
        verify(jpaBookingRepository).findAllByOwnerId(1L);
    }
}
