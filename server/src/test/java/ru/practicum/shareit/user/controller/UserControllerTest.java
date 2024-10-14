package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

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
class UserControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final JpaUserRepository jpaUserRepository;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        String userDtoJson = mapper.writeValueAsString(userDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andDo(print());
    }

    @Test
    void updateUser_shouldReturnUpdateUser() throws Exception {
        User oldUser = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        oldUser = jpaUserRepository.save(oldUser);

        UserDto newUserDto = UserDto.builder()
                .name("John")
                .email("john@example.com")
                .build();

        String userDtoJson = mapper.writeValueAsString(newUserDto);

        mockMvc.perform(patch("/users/{userId}", oldUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andDo(print());
    }

    @Test
    void getUserById_shouldReturnUserById() throws Exception {
        User userDto = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        userDto = jpaUserRepository.save(userDto);

        mockMvc.perform(get("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andDo(print());
    }


    @Test
    void deleteUserById_shouldDeleteUser() throws Exception {
        User userDto = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        userDto = jpaUserRepository.save(userDto);

        mockMvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/users/{userId}", userDto.getId()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getAllUsers_shouldReturnListAllUsers() throws Exception {
        User userOne = User.builder()
                .name("first User")
                .email("firstUser@example.com")
                .build();
        jpaUserRepository.save(userOne);

        User userTwo = User.builder()
                .name("second User")
                .email("secondUser@example.com")
                .build();
        jpaUserRepository.save(userTwo);

        User userThree = User.builder()
                .name("third User")
                .email("thirdUser@example.com")
                .build();
        jpaUserRepository.save(userThree);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$[0].name", is("first User")))
                .andExpect(jsonPath("$[0].email", is("firstUser@example.com")))
                .andExpect(jsonPath("$[1].name", is("second User")))
                .andExpect(jsonPath("$[1].email", is("secondUser@example.com")))
                .andExpect(jsonPath("$[2].name", is("third User")))
                .andExpect(jsonPath("$[2].email", is("thirdUser@example.com")))
                .andDo(print());
    }

    @Test
    void deleteAllUsers_shouldDeleteAllUsers() throws Exception {
        User userOne = User.builder()
                .name("first User")
                .email("firstUser@example.com")
                .build();
        jpaUserRepository.save(userOne);

        User userTwo = User.builder()
                .name("second User")
                .email("secondUser@example.com")
                .build();
        jpaUserRepository.save(userTwo);

        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)))
                .andDo(print());
    }
}