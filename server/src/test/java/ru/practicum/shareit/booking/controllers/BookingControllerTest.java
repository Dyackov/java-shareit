package ru.practicum.shareit.booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final JpaBookingRepository jpaBookingRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaItemRepository jpaItemRepository;

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        User booker = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        String bookingDtoRequestJson = mapper.writeValueAsString(bookingDtoRequest);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingDtoRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.item.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booker.getId().intValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())))
                .andDo(print());
    }


    @Test
    void confirmOrRejectBooking_shouldReturnUpdatedBooking() throws Exception {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        User booker = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        booking = jpaBookingRepository.save(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booker.getId().intValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())))
                .andDo(print());
    }

    @Test
    void getBookingById_shouldReturnBookingById() throws Exception {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        User booker = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
        booking = jpaBookingRepository.save(booking);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booker.getId().intValue())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andDo(print());
    }

    @Test
    void getAllByBookerIdAndState_shouldReturnAllBookings() throws Exception {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        User booker = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
        jpaBookingRepository.save(booking);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].item.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$[0].booker.id", is(booker.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.toString())))
                .andDo(print());
    }

    @Test
    void getAllByOwnerId_shouldReturnAllOwnerBookings() throws Exception {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        User booker = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
        jpaBookingRepository.save(booking);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].item.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$[0].booker.id", is(booker.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.toString())))
                .andDo(print());
    }
}