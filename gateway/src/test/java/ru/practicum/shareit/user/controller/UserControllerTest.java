package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserClient;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private final UserClient userClient;

    @Test
    void createUser_whenValidInput_thenReturnsStatusCreated() throws Exception {
        UserDto validUser = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userClient).createUserDto(validUser);
    }

    @Test
    void createUser_withEmptyName_shouldReturnBadRequest() throws Exception {
        UserDto validUser = UserDto.builder()
                .email("john.doe@example.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(userClient);
    }

    @Test
    void createUser_withEmptyEmail_shouldReturnBadRequest() throws Exception {
        UserDto validUser = UserDto.builder()
                .name("John Doe")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(userClient);
    }

    @Test
    void createUser_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        UserDto validUser = UserDto.builder()
                .name("John Doe")
                .email("john.doe")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(userClient);
    }

    @Test
    void updateUser_withValidData_shouldReturnOk() throws Exception {
        long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        mockMvc.perform(patch("/users/{userId}",userId)
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userClient).updateUserDto(userId, userDto);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void getUserById_withValidId_shouldReturnOk() throws Exception {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}",userId))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userClient).getUserByIdDto(userId);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void deleteUserById_withValidId_shouldReturnNoContent() throws Exception {
        long userId = 1L;
        mockMvc.perform(delete("/users/{userId}",userId))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userClient).deleteUserById(userId);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userClient).getAllUsersDto();
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void deleteAllUsers_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userClient).deleteAllUsers();
        verifyNoMoreInteractions(userClient);
    }
}