package ru.practicum.shareit.booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingClient;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({BookingController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private final BookingClient bookingClient;


    @Test
    void createBooking_whenValidInput_thenReturnsStatusOk() throws Exception {
        long userId = 1L;
        BookingDtoRequest validBooking = BookingDtoRequest.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBooking)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient).createBooking(userId, validBooking);
    }

    @Test
    void createBooking_withNullItemId_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        BookingDtoRequest invalidBooking = BookingDtoRequest.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_withNullStartTime_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        BookingDtoRequest invalidBooking = BookingDtoRequest.builder()
                .itemId(2L)
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_withNullEndTime_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        BookingDtoRequest invalidBooking = BookingDtoRequest.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void confirmOrRejectBooking_whenApproved_shouldReturnStatusOk() throws Exception {
        long userId = 1L;
        long bookingId = 2L;
        boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient).confirmOrRejectBooking(userId, bookingId, approved);
    }

    @Test
    void getBookingById_whenValidId_shouldReturnStatusOk() throws Exception {
        long userId = 1L;
        long bookingId = 2L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient).getBookingById(userId, bookingId);
    }

    @Test
    void getAllByBookerIdAndState_whenValidUserId_shouldReturnStatusOk() throws Exception {
        long userId = 1L;
        String state = "ALL";

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient).getAllByBookerIdAndState(userId, BookingState.fromString(state));
    }

    @Test
    void getAllByBookerId_whenValidUserId_shouldReturnStatusOk() throws Exception {
        long userId = 1L;
        String state = "ALL";

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient).getAllByBookerId(userId, BookingState.fromString(state));
    }

    @Test
    void getAllByBookerId_whenInvalidState_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        String invalidState = "INVALID_STATE";

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", invalidState)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(bookingClient);
    }
}