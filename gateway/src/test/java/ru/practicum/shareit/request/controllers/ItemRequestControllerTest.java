package ru.practicum.shareit.request.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemRequestController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private final RequestClient requestClient;

    @Test
    void createItemRequest_whenValidRequest_shouldReturnCreated() throws Exception {
        long userId = 1L;
        ItemRequestDto validRequest = ItemRequestDto.builder()
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .build();

        when(requestClient.createItemRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(validRequest));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andDo(print());

        verify(requestClient).createItemRequest(userId, validRequest);
    }


    @Test
    void createItemRequest_withEmptyDescription_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        ItemRequestDto invalidRequest = ItemRequestDto.builder()
                .description(null)
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(requestClient);
    }


    @Test
    void getOwnRequests_whenUserExists_shouldReturnRequests() throws Exception {
        long userId = 1L;
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder().id(1L).description("Нужна дрель").build(),
                ItemRequestDto.builder().id(2L).description("Нужен шлифовальный станок").build()
        );

        when(requestClient.getOwnRequests(userId)).thenReturn(ResponseEntity.ok(requests));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());

        verify(requestClient).getOwnRequests(userId);
    }


    @Test
    void getAllRequests_whenUserExists_shouldReturnAllRequests() throws Exception {
        long userId = 1L;
        List<ItemRequestDto> allRequests = List.of(
                ItemRequestDto.builder().id(1L).description("Нужна дрель").build(),
                ItemRequestDto.builder().id(2L).description("Нужен шлифовальный станок").build()
        );

        when(requestClient.getAllRequests(userId)).thenReturn(ResponseEntity.ok(allRequests));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());

        verify(requestClient).getAllRequests(userId);
    }

    @Test
    void getRequestsById_whenValidRequestId_shouldReturnRequest() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDto request = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужна дрель")
                .build();

        when(requestClient.getRequestsById(userId, requestId)).thenReturn(ResponseEntity.ok(request));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andDo(print());

        verify(requestClient).getRequestsById(userId, requestId);
    }
}