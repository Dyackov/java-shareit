package ru.practicum.shareit.request.controllers;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemRequestControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final JpaUserRepository jpaUserRepository;

    @Test
    void createItemRequest_shouldReturnCreatedRequest() throws Exception {
        User requestor = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        requestor = jpaUserRepository.save(requestor);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Request for item")
                .build();

        String itemRequestDtoJson = mapper.writeValueAsString(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemRequestDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andDo(print());
    }

    @Test
    void getOwnRequests_shouldReturnOwnRequests() throws Exception {
        User requestor = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        requestor = jpaUserRepository.save(requestor);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("Request 1")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Request 2")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
        jpaItemRequestRepository.save(itemRequest1);
        jpaItemRequestRepository.save(itemRequest2);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)))
                .andDo(print());
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        User requestor1 = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        requestor1 = jpaUserRepository.save(requestor1);

        User requestor2 = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        requestor2 = jpaUserRepository.save(requestor2);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("Request from John")
                .created(LocalDateTime.now())
                .requestor(requestor1)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Request from Jane")
                .created(LocalDateTime.now())
                .requestor(requestor2)
                .build();
        jpaItemRequestRepository.save(itemRequest1);
        jpaItemRequestRepository.save(itemRequest2);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requestor2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequest1.getDescription())))
                .andDo(print());
    }

    @Test
    void getRequestsById_shouldReturnRequestById() throws Exception {
        User requestor = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        requestor = jpaUserRepository.save(requestor);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Request for item")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
        itemRequest = jpaItemRequestRepository.save(itemRequest);

        mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequest.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andDo(print());
    }

    @Test
    void createItemRequest_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Request for item")
                .build();

        String itemRequestDtoJson = mapper.writeValueAsString(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 999) // несуществующий пользователь
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemRequestDtoJson))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}