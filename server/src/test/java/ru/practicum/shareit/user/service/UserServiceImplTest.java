package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private JpaItemRepository jpaItemRepository;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }


    @Test
    void createUserDto() {
        when(jpaUserRepository.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userServiceImpl.createUserDto(userDto);

        assertNotNull(createdUser);
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        verify(jpaUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserDto() {
        when(jpaUserRepository.save(any(User.class))).thenReturn(user);
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserDto updatedUser = userServiceImpl.updateUserDto(1, userDto);

        assertNotNull(updatedUser);
        assertEquals(userDto.getName(), updatedUser.getName());
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        verify(jpaUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserByIdDto() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        UserDto userDto = userServiceImpl.getUserByIdDto(1L);

        assertNotNull(userDto);
        assertEquals(userDto.getId(), 1L);
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
        verify(jpaUserRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUserById() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        userServiceImpl.deleteUserById(1L);

        verify(jpaUserRepository, times(1)).deleteById(1L);
        verify(jpaItemRepository, times(1)).deleteAllByOwnerId(1L);
    }

    @Test
    void getAllUsersDto() {
        when(jpaUserRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> users = userServiceImpl.getAllUsersDto();

        assertNotNull(users);
        assertEquals(users.size(), 1);
        assertEquals(users.getFirst().getId(), 1L);
        assertEquals(users.getFirst().getName(), user.getName());
        assertEquals(users.getFirst().getEmail(), user.getEmail());
        verify(jpaUserRepository, times(1)).findAll();
    }

    @Test
    void deleteAllUsers() {
        userServiceImpl.deleteAllUsers();
        verify(jpaUserRepository, times(1)).deleteAll();
        verify(jpaItemRepository, times(1)).deleteAll();

    }

    @Test
    void checkUserExist() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        User checkUser = userServiceImpl.checkUserExist(1L);
        assertNotNull(checkUser);
        assertEquals(checkUser.getId(), 1L);
        assertEquals(checkUser.getName(), user.getName());
        assertEquals(checkUser.getEmail(), user.getEmail());
        verify(jpaUserRepository, times(1)).findById(1L);
        assertThrows(NotFoundException.class, () -> userServiceImpl.checkUserExist(999L));
    }
}