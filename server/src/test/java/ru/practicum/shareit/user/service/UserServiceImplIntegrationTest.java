package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final UserServiceImpl userServiceImpl;
    private final JpaUserRepository jpaUserRepository;
    private final JpaItemRepository jpaItemRepository;

    @Test
    void createUserDto_shouldCreateUserSuccessfully() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto createdUserDto = userServiceImpl.createUserDto(userDto);

        assertThat(createdUserDto).isNotNull();
        assertThat(createdUserDto.getId()).isNotNull();
        assertThat(createdUserDto.getName()).isEqualTo("John Doe");
        assertThat(createdUserDto.getEmail()).isEqualTo("john.doe@example.com");

        User savedUser = jpaUserRepository.findById(createdUserDto.getId()).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser != null ? savedUser.getName() : null).isEqualTo("John Doe");
        assertThat(savedUser != null ? savedUser.getEmail() : null).isEqualTo("john.doe@example.com");
    }

    @Test
    void updateUserDto_shouldUpdateUserDtoSuccessfully() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto updateUserDto = UserDto.builder()
                .name("Updated John Doe")
                .email("updatedJohn.doe@example.com")
                .build();

        UserDto createdUserDto = userServiceImpl.createUserDto(userDto);
        UserDto updatedUserDto = userServiceImpl.updateUserDto(createdUserDto.getId(), updateUserDto);

        assertThat(updatedUserDto).isNotNull();
        assertThat(updatedUserDto.getName()).isEqualTo("Updated John Doe");
        assertThat(updatedUserDto.getEmail()).isEqualTo("updatedJohn.doe@example.com");

        User savedUser = jpaUserRepository.findById(updatedUserDto.getId()).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser != null ? savedUser.getId() : null).isEqualTo(updatedUserDto.getId());
        assertThat(savedUser != null ? savedUser.getName() : null).isEqualTo("Updated John Doe");
        assertThat(savedUser != null ? savedUser.getEmail() : null).isEqualTo("updatedJohn.doe@example.com");
    }

    @Test
    void getUserByIdDto_ShouldReturnUserByIdDto() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto createdUserDto = userServiceImpl.createUserDto(userDto);
        UserDto getUserByIdDto = userServiceImpl.getUserByIdDto(createdUserDto.getId());

        assertThat(getUserByIdDto).isNotNull();
        assertThat(getUserByIdDto.getName()).isEqualTo("John Doe");
        assertThat(getUserByIdDto.getEmail()).isEqualTo("john.doe@example.com");

        User getUser = jpaUserRepository.findById(getUserByIdDto.getId()).orElse(null);
        assertThat(getUser).isNotNull();

        assertThat(getUser != null ? getUser.getId() : null).isEqualTo(getUserByIdDto.getId());
        assertThat(getUser != null ? getUser.getName() : null).isEqualTo("John Doe");
        assertThat(getUser != null ? getUser.getEmail() : null).isEqualTo("john.doe@example.com");
    }

    @Test
    void getUserByIdDto_ShouldThrowNotFoundException_WhenRequestNotFound() {
        long userId = 111L;
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                userServiceImpl.getUserByIdDto(userId)
        );
        assertEquals("Пользователя с ID: " + userId + " не существует.", thrown.getMessage());
    }

    @Test
    void deleteUserById_shouldDeleteUserAndItemsSuccessfully() {
        User user = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .owner(user)
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .owner(user)
                .available(true)
                .build();

        jpaItemRepository.save(item1);
        jpaItemRepository.save(item2);

        assertTrue(jpaUserRepository.findById(user.getId()).isPresent());
        assertEquals(2, jpaItemRepository.findAllByOwnerId(user.getId()).size());

        userServiceImpl.deleteUserById(user.getId());

        Optional<User> deletedUser = jpaUserRepository.findById(user.getId());
        assertTrue(deletedUser.isEmpty(), "Пользователь должен быть удалён.");

        assertEquals(0, jpaItemRepository.findAllByOwnerId(user.getId()).size(),
                "Все вещи пользователя должны быть удалены.");
    }


    @Test
    void getAllUsersDto_ShouldReturnListOfAllUsers() {
        User userOne = User.builder()
                .name("first User")
                .email("firstUser@example.com")
                .build();

        User userTwo = User.builder()
                .name("second User")
                .email("secondUser@example.com")
                .build();

        User userThree = User.builder()
                .name("third User")
                .email("thirdUser@example.com")
                .build();

        jpaUserRepository.save(userOne);
        jpaUserRepository.save(userTwo);
        jpaUserRepository.save(userThree);

        List<UserDto> usersDto = userServiceImpl.getAllUsersDto();

        assertThat(usersDto.size()).isEqualTo(3);
        assertThat(usersDto.get(0).getName()).isEqualTo("first User");
        assertThat(usersDto.get(0).getEmail()).isEqualTo("firstUser@example.com");
        assertThat(usersDto.get(1).getName()).isEqualTo("second User");
        assertThat(usersDto.get(1).getEmail()).isEqualTo("secondUser@example.com");
        assertThat(usersDto.get(2).getName()).isEqualTo("third User");
        assertThat(usersDto.get(2).getEmail()).isEqualTo("thirdUser@example.com");
    }

    @Test
    void deleteAllUsers_shouldDeleteAllUsersAndItemsSuccessfully() {
        User user1 = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .build();
        user1 = jpaUserRepository.save(user1);

        User user2 = User.builder()
                .name("Test User Two")
                .email("testuserTwo@example.com")
                .build();
        user2 = jpaUserRepository.save(user2);

        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .owner(user1)
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .owner(user1)
                .available(true)
                .build();

        jpaItemRepository.save(item1);
        jpaItemRepository.save(item2);
        assertEquals(2, jpaUserRepository.findAll().size());
        assertEquals(2, jpaItemRepository.findAll().size());
        assertTrue(jpaUserRepository.findById(user1.getId()).isPresent());
        assertTrue(jpaUserRepository.findById(user2.getId()).isPresent());
        assertTrue(jpaItemRepository.findById(item1.getId()).isPresent());
        assertTrue(jpaItemRepository.findById(item2.getId()).isPresent());

        assertEquals(2, jpaItemRepository.findAllByOwnerId(user1.getId()).size());
        assertEquals(0, jpaItemRepository.findAllByOwnerId(user2.getId()).size());

        userServiceImpl.deleteAllUsers();

        List<User> deletedUsers = jpaUserRepository.findAll();
        assertTrue(deletedUsers.isEmpty(), "Пользователи должны быть удалены.");

        List<Item> deletedItems = jpaItemRepository.findAll();
        assertTrue(deletedItems.isEmpty(), "Вещи должны быть удалены.");
        assertEquals(0, jpaItemRepository.findAllByOwnerId(user1.getId()).size(),
                "Все вещи пользователя должны быть удалены.");
    }

    @Test
    void checkUserExist_ShouldReturnUser_WhenUserExists() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        user = jpaUserRepository.save(user);

        UserDto userDto = userServiceImpl.getUserByIdDto(user.getId());

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void checkUserExist_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        long userId = 999L;
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                userServiceImpl.getUserByIdDto(userId)
        );
        assertEquals("Пользователя с ID: " + userId + " не существует.", thrown.getMessage());
    }
}
