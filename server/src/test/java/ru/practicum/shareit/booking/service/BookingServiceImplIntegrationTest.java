package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingServiceImpl bookingServiceImpl;
    private final JpaBookingRepository jpaBookingRepository;
    private final JpaItemRepository jpaItemRepository;
    private final JpaUserRepository jpaUserRepository;

    @Test
    public void testCreateBooking_success() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto bookingDto = bookingServiceImpl.createBooking(booker.getId(), bookingRequest);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testCreateBooking_throwsNotFoundException_ifOwnerTriesToBook() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingServiceImpl.createBooking(owner.getId(), bookingRequest)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Вы не можете создать бронирование с тем же идентификатором владельца.");
    }

    @Test
    public void testCreateBooking_throwsValidationException_ifItemNotAvailable() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(false).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.createBooking(booker.getId(), bookingRequest)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Ошибка, вещь недоступна.");
    }


    @Test
    public void testCreateBooking_itemUnavailable() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(false).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.createBooking(booker.getId(), bookingRequest)
        );

        assertThat(exception.getMessage()).contains("вещь недоступна");
    }


    @Test
    public void testConfirmOrRejectBooking_success() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        BookingDto bookingDto = bookingServiceImpl.confirmOrRejectBooking(owner.getId(), booking.getId(), true);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void testConfirmOrRejectBooking_successReject() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        BookingDto bookingDto = bookingServiceImpl.confirmOrRejectBooking(owner.getId(), booking.getId(), false);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    public void testConfirmOrRejectBooking_alreadyApproved() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.confirmOrRejectBooking(owner.getId(), booking.getId(), true)
        );

        assertThat(exception.getMessage()).contains("Бронирование уже подтверждено");
    }

    @Test
    public void testConfirmOrRejectBooking_successApprove() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        BookingDto bookingDto = bookingServiceImpl.confirmOrRejectBooking(owner.getId(), booking.getId(), true);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void testConfirmOrRejectBooking_forbidden() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        User anotherUser = jpaUserRepository.save(User.builder().name("Another").email("another@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(BookingStatus.WAITING)
                .build());

        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->
                bookingServiceImpl.confirmOrRejectBooking(anotherUser.getId(), booking.getId(), true)
        );

        assertThat(exception.getMessage()).contains("Ошибка авторизации");
    }


    @Test
    public void testGetBookingById_success() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        BookingDto bookingDto = bookingServiceImpl.getBookingById(booker.getId(), booking.getId());

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetBookingById_notFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingServiceImpl.getBookingById(1L, 999L)
        );

        assertThat(exception.getMessage()).contains("не существует");
    }

    @Test
    public void testGetBookingState_All() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getBookingState(booker.getId(), BookingState.ALL);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetBookingState_Current() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getBookingState(booker.getId(), BookingState.CURRENT);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetBookingState_Past() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getBookingState(booker.getId(), BookingState.PAST);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetBookingState_Future() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getBookingState(booker.getId(), BookingState.FUTURE);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetBookingState_Waiting() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getBookingState(booker.getId(), BookingState.WAITING);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetBookingState_Rejected() {
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(booker).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getBookingState(booker.getId(), BookingState.REJECTED);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetAllByOwnerId_All() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item1 = jpaItemRepository.save(Item.builder().name("Item 1").available(true).owner(owner).build());
        Item item2 = jpaItemRepository.save(Item.builder().name("Item 2").available(true).owner(owner).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1)
                .booker(jpaUserRepository.save(User.builder().name("Booker 1").email("booker1@example.com").build()))
                .status(BookingStatus.WAITING)
                .build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .item(item2)
                .booker(jpaUserRepository.save(User.builder().name("Booker 2").email("booker2@example.com").build()))
                .status(BookingStatus.REJECTED)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getAllByOwnerId(owner.getId(), BookingState.ALL);

        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());

    }

    @Test
    public void testGetAllByOwnerId_Current() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build()))
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getAllByOwnerId(owner.getId(), BookingState.CURRENT);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetAllByOwnerId_Past() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build()))
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getAllByOwnerId(owner.getId(), BookingState.PAST);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetAllByOwnerId_Future() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build()))
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getAllByOwnerId(owner.getId(), BookingState.FUTURE);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetAllByOwnerId_Waiting() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build()))
                .status(BookingStatus.WAITING)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getAllByOwnerId(owner.getId(), BookingState.WAITING);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testGetAllByOwnerId_Rejected() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build()))
                .status(BookingStatus.REJECTED)
                .build());

        List<BookingDto> bookings = bookingServiceImpl.getAllByOwnerId(owner.getId(), BookingState.REJECTED);

        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testCheckBookingExist_NotFound() {
        long nonExistentBookingId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingServiceImpl.checkBookingExist(nonExistentBookingId));

        assertThat(exception.getMessage()).contains("не существует");
    }

    @Test
    public void testCheckUserAuthorization_successForBooker() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        bookingServiceImpl.checkUserAuthorizationForBooking(booker.getId(), booking);
    }

    @Test
    public void testCheckUserAuthorization_successForOwner() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        bookingServiceImpl.checkUserAuthorizationForBooking(owner.getId(), booking);
    }

    @Test
    public void testCheckUserAuthorization_failure() {
        User owner = jpaUserRepository.save(User.builder().name("Owner").email("owner@example.com").build());
        User booker = jpaUserRepository.save(User.builder().name("Booker").email("booker@example.com").build());
        User anotherUser = jpaUserRepository.save(User.builder().name("AnotherUser").email("another@example.com").build());
        Item item = jpaItemRepository.save(Item.builder().name("Item").available(true).owner(owner).build());

        Booking booking = jpaBookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingServiceImpl.checkUserAuthorizationForBooking(anotherUser.getId(), booking)
        );

        assertThat(exception.getMessage()).contains("Ошибка авторизации");
    }

    @Test
    public void testCheckTime_throwsValidationException_ifEndTimeBeforeNow() {
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.createBooking(1L, bookingRequest)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Время конца не может быть раньше текущего времени.");
    }

    @Test
    public void testCheckTime_throwsValidationException_ifEndTimeBeforeStartTime() {
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.createBooking(1L, bookingRequest)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Время конца не может быть раньше времени начала.");
    }

    @Test
    public void testCheckTime_throwsValidationException_ifStartTimeEqualsEndTime() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .start(sameTime)
                .end(sameTime)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.createBooking(1L, bookingRequest)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Время начала не может быть равно времени конца.");
    }

    @Test
    public void testCheckTime_throwsValidationException_ifStartTimeBeforeNow() {
        BookingDtoRequest bookingRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingServiceImpl.createBooking(1L, bookingRequest)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Время начала не может быть раньше текущего времени.");
    }
}