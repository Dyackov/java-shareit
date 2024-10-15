package ru.practicum.shareit.item.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private final ItemClient itemClient;

    @Test
    void createItem_whenValidInput_thenReturnsStatusOk() throws Exception {
        long userId = 1L;
        ItemDto validItemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .requestId(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient).createItem(userId, validItemDto);
    }

    @Test
    void createItem_withEmptyName_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        ItemDto validItemDto = ItemDto.builder()
                .description("Item Description")
                .available(true)
                .requestId(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_withEmptyDescription_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        ItemDto validItemDto = ItemDto.builder()
                .name("Item Name")
                .available(true)
                .requestId(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_withEmptyAvailable_shouldReturnBadRequest() throws Exception {
        long userId = 1L;
        ItemDto validItemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .requestId(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(itemClient);
    }

    @Test
    void updateItem_whenValidInput_thenReturnsStatusOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemDto validItemDto = ItemDto.builder()
                .name("Updated Item Name")
                .description("Updated Item Description")
                .available(true)
                .requestId(null)
                .build();

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient).updateItem(userId, itemId, validItemDto);
    }

    @Test
    void getItemById_whenValidInput_thenReturnsStatusOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .requestId(null)
                .build();

        when(itemClient.getItemById(userId, itemId)).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)))
                .andDo(print());

        verify(itemClient).getItemById(userId, itemId);
    }

    @Test
    void getItemById_withNonExistingItem_shouldReturnNotFound() throws Exception {
        long userId = 1L;
        long itemId = 999L;

        when(itemClient.getItemById(userId, itemId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(itemClient).getItemById(userId, itemId);
    }

    @Test
    void getAllItemsFromUser_whenValidInput_thenReturnsStatusOk() throws Exception {
        long userId = 1L;

        List<ItemDto> itemList = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Item 1")
                        .description("Description 1")
                        .available(true)
                        .requestId(null)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Item 2")
                        .description("Description 2")
                        .available(true)
                        .requestId(null)
                        .build()
        );

        doReturn(ResponseEntity.ok(itemList)).when(itemClient).getAllItemsFromUser(userId);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemList)))
                .andDo(print());

        verify(itemClient).getAllItemsFromUser(userId);
    }

    @Test
    void searchAvailableItemsByText_whenValidInput_thenReturnsStatusOk() throws Exception {
        long userId = 1L;
        String searchText = "Item";

        List<ItemDto> availableItems = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Available Item 1")
                        .description("Description of available item 1")
                        .available(true)
                        .requestId(null)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Available Item 2")
                        .description("Description of available item 2")
                        .available(true)
                        .requestId(null)
                        .build()
        );

        doReturn(ResponseEntity.ok(availableItems)).when(itemClient).searchAvailableItemsByText(userId, searchText);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(availableItems)))
                .andDo(print());

        verify(itemClient).searchAvailableItemsByText(userId, searchText);
    }

    @Test
    void deleteItemById_whenValidInput_thenReturnsStatusNoContent() throws Exception {
        long userId = 1L;
        long itemId = 2L;
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient).deleteItemById(userId, itemId);
    }

    @Test
    void deleteAllItemsByUser_whenValidInput_thenReturnsStatusNoContent() throws Exception {
        long userId = 1L;
        mockMvc.perform(delete("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient).deleteAllItemsByUser(userId);
    }


    @Test
    void createComment_whenValidInput_thenReturnsStatusOk() throws Exception {
        long authorId = 1L;
        long itemId = 2L;
        CommentDtoRequest validComment = CommentDtoRequest.builder()
                .text("Это комментарий.")
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient).createComment(authorId, itemId, validComment);
    }
}
