package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private JpaItemRequestRepository jpaItemRequestRepository;

    @Mock
    private UserService userServiceImpl;

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("User").email("user@example.com").build();
        itemRequestDto = ItemRequestDto.builder().description("Need a book").build();
        itemRequest = ItemRequest.builder().id(1L).description("Need a book").created(LocalDateTime.now()).requestor(user).build();
        item = Item.builder().id(1L).name("Book").description("A great book").request(itemRequest).build();
    }

    @Test
    void createItemRequest_ShouldCreateRequest() {
        when(userServiceImpl.checkUserExist(anyLong())).thenReturn(user);
        when(jpaItemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        verify(jpaItemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getOwnRequests_ShouldReturnOwnRequests() {
        when(userServiceImpl.checkUserExist(anyLong())).thenReturn(user);
        when(jpaItemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));
        when(jpaItemRepository.findAllByRequestIds(any())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getOwnRequests(user.getId());

        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.getFirst().getId());
        assertEquals(itemRequest.getDescription(), result.getFirst().getDescription());

        verify(userServiceImpl, times(1)).checkUserExist(user.getId());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() {
        when(userServiceImpl.checkUserExist(anyLong())).thenReturn(user);
        when(jpaItemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));
        when(jpaItemRepository.findAllByRequestIds(any())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId());

        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.getFirst().getId());
        assertEquals(itemRequest.getDescription(), result.getFirst().getDescription());
        assertEquals(1, result.getFirst().getItems().size());
        verify(userServiceImpl, times(1)).checkUserExist(user.getId());
    }

    @Test
    void getRequestsById_ShouldReturnRequest() {
        when(userServiceImpl.checkUserExist(anyLong())).thenReturn(user);
        when(jpaItemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(jpaItemRepository.findItemsByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getRequestsById(user.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        verify(userServiceImpl, times(1)).checkUserExist(user.getId());
    }

    @Test
    void getRequestsById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(userServiceImpl.checkUserExist(anyLong())).thenReturn(user);
        when(jpaItemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestsById(user.getId(), 999L)
        );

        assertEquals("Запроса с ID: 999 не существует.", thrown.getMessage());
    }

    @Test
    void checkItemRequestExist_ShouldReturnRequest() {
        when(jpaItemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest result = itemRequestService.checkItemRequestExist(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(jpaItemRequestRepository, times(1)).findById(itemRequest.getId());
    }

    @Test
    void checkItemRequestExist_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(jpaItemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                itemRequestService.checkItemRequestExist(999L)
        );

        assertEquals("Запроса с ID: 999 не существует.", thrown.getMessage());
    }
}
