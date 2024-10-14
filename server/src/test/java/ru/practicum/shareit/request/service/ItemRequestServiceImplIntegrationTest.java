package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;




@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestServiceImpl itemRequestServiceImpl;
    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaItemRepository jpaItemRepository;

    @Test
    public void createItemRequest_ShouldCreateItemRequest_WhenUserExists() {
        User user = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .build();
        jpaUserRepository.save(user);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Test Item Request")
                .build();

        ItemRequestDto createdItemRequest = itemRequestServiceImpl.createItemRequest(user.getId(), itemRequestDto);

        assertThat(createdItemRequest).isNotNull();
        assertThat(createdItemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());

        ItemRequest foundRequest = jpaItemRequestRepository.findById(createdItemRequest.getId()).orElse(null);
        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(createdItemRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(foundRequest.getRequestor().getId()).isEqualTo(user.getId());
    }


    @Test
    void testGetOwnRequests_ShouldGetOwnRequests() {
        User user = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .build();

        jpaUserRepository.save(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Test request")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        jpaItemRequestRepository.save(itemRequest);

        List<ItemRequestDto> requests = itemRequestServiceImpl.getOwnRequests(user.getId());

        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(requests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void testGetAllRequests_whenUserExists_shouldReturnRequestsExcludingOwn() {
        User user1 = User.builder()
                .name("User One")
                .email("userone@example.com")
                .build();

        User user2 = User.builder()
                .name("User Two")
                .email("usertwo@example.com")
                .build();

        jpaUserRepository.save(user1);
        jpaUserRepository.save(user2);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("Request from User One")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Request from User Two")
                .requestor(user2)
                .created(LocalDateTime.now().plusHours(1))
                .build();

        jpaItemRequestRepository.save(itemRequest1);
        jpaItemRequestRepository.save(itemRequest2);

        List<ItemRequestDto> requests = itemRequestServiceImpl.getAllRequests(user2.getId());

        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.getFirst().getId()).isEqualTo(itemRequest1.getId());
        assertThat(requests.getFirst().getDescription()).isEqualTo(itemRequest1.getDescription());
    }

    @Test
    void testGetRequestsById_whenRequestExists_shouldReturnRequestWithItems() {
        User user = jpaUserRepository.save(User.builder()
                .email("user@example.com")
                .name("User")
                .build());

        ItemRequest itemRequest = jpaItemRequestRepository.save(ItemRequest.builder()
                .description("Test request")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());

        Item item = jpaItemRepository.save(Item.builder()
                .name("Item1")
                .description("Test item")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build());

        ItemRequestDto foundRequest = itemRequestServiceImpl.getRequestsById(user.getId(), itemRequest.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(itemRequest.getId());
        assertThat(foundRequest.getItems()).isNotNull();
        assertThat(foundRequest.getItems().size()).isEqualTo(1);
        assertThat(foundRequest.getItems().getFirst().getId()).isEqualTo(item.getId());
    }

    @Test
    public void testCheckItemRequestExist_success() {
        User user = jpaUserRepository.save(User.builder().name("Test User").email("test@example.com").build());

        ItemRequest itemRequest = jpaItemRequestRepository.save(ItemRequest.builder()
                .description("Test request")
                .requestor(user)
                .build());

        ItemRequest foundRequest = itemRequestServiceImpl.checkItemRequestExist(itemRequest.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    public void testCheckItemRequestExist_notFound() {
        long nonExistentRequestId = 9999L;

        assertThatThrownBy(() -> itemRequestServiceImpl.checkItemRequestExist(nonExistentRequestId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запроса с ID: " + nonExistentRequestId + " не существует.");
    }
}