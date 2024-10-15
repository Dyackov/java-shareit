package ru.practicum.shareit.item.controllers;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
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
class ItemControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final JpaItemRepository jpaItemRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaBookingRepository jpaBookingRepository;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        ItemDto itemDto = ItemDto.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .build();

        String itemDtoJson = mapper.writeValueAsString(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name", is("Item 1")))
                .andExpect(jsonPath("$.description", is("Description for Item 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andDo(print());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);

        ItemDto updatedItemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated description")
                .available(false)
                .build();

        String updatedItemDtoJson = mapper.writeValueAsString(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedItemDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name", is("Updated Item")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.available", is(false)))
                .andDo(print());
    }

    @Test
    void getItemById_shouldReturnItemById() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name", is("Item 1")))
                .andExpect(jsonPath("$.description", is("Description for Item 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andDo(print());
    }

    @Test
    void deleteItemById_shouldDeleteItem() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);

        mockMvc.perform(delete("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getAllItemsFromUser_shouldReturnAllItems() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item itemOne = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(itemOne);

        Item itemTwo = Item.builder()
                .name("Item 2")
                .description("Description for Item 2")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(itemTwo);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].name", is("Item 2")))
                .andDo(print());
    }

    @Test
    void deleteAllItems_shouldDeleteAllItems() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item itemOne = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(itemOne);

        Item itemTwo = Item.builder()
                .name("Item 2")
                .description("Description for Item 2")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(itemTwo);

        mockMvc.perform(delete("/items")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)))
                .andDo(print());
    }

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        jpaBookingRepository.save(booking);

        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("Great item!")
                .build();

        String commentDtoRequestJson = mapper.writeValueAsString(commentDtoRequest);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.text", is("Great item!")))
                .andDo(print());
    }


    @Test
    void searchAvailableItemsByText_shouldReturnAvailableItems() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item itemOne = Item.builder()
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(itemOne);

        Item itemTwo = Item.builder()
                .name("Item 2")
                .description("Description for Item 2")
                .available(false)
                .owner(user)
                .build();
        jpaItemRepository.save(itemTwo);

        mockMvc.perform(get("/items/search?text=Item")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andDo(print());
    }
}