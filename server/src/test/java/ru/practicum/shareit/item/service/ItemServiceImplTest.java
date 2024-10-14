package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaCommentRepository;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Mock
    private JpaItemRepository jpaItemRepository;
    @Mock
    private JpaBookingRepository jpaBookingRepository;
    @Mock
    private JpaCommentRepository jpaCommentRepository;
    @Mock
    private ItemRequestServiceImpl itemRequestServiceImpl;
    @Mock
    private UserServiceImpl userServiceImpl;

    private Item item;
    private ItemDto itemDto;
    private User user;
    private Comment comment;
    private CommentDtoRequest commentDtoRequest;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Молоток")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .request(null)
                .build();


        itemDto = ItemDto.builder()
                .id(1L)
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .requestId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 8, 10, 0))
                .end(LocalDateTime.of(2024, 10, 8, 12, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        comment = Comment.builder()
                .text("Крепкий молоток")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2024, 10, 8, 21, 0))
                .build();

        commentDtoRequest = new CommentDtoRequest("Крепкий молоток");
    }


    @Test
    void createItem() {
        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaItemRepository.save(any(Item.class))).thenReturn(item);
        when(itemRequestServiceImpl.checkItemRequestExist(anyLong())).thenReturn(itemRequest);

        ItemDto createdItem = itemServiceImpl.createItem(1L, itemDto);
        assertNotNull(createdItem);
        assertEquals(item.getId(), createdItem.getId());
        assertEquals(item.getName(), createdItem.getName());
        assertEquals(item.getDescription(), createdItem.getDescription());
        assertEquals(item.getAvailable(), createdItem.getAvailable());
        verify(jpaItemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaItemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updatedItem = itemServiceImpl.updateItem(1L, 1L, itemDto);

        assertNotNull(updatedItem);
        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(item.getName(), updatedItem.getName());
        assertEquals(item.getDescription(), updatedItem.getDescription());
        assertEquals(item.getAvailable(), updatedItem.getAvailable());
        verify(jpaItemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void getItemById() {
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));

        ItemDtoBooking getItem = itemServiceImpl.getItemById(1L, 1L);

        assertNotNull(getItem);
        assertEquals(item.getId(), getItem.getId());
        assertEquals(item.getName(), getItem.getName());
        assertEquals(item.getDescription(), getItem.getDescription());
        assertEquals(item.getAvailable(), getItem.getAvailable());
        verify(jpaItemRepository, times(1)).findById(1L);

    }

    @Test
    void getAllItemsFromUser() {
        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        when(jpaItemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemDtoBooking> AllItemsFromUser = itemServiceImpl.getAllItemsFromUser(1L);

        assertNotNull(AllItemsFromUser);
        assertEquals(item.getId(), AllItemsFromUser.getFirst().getId());
        assertEquals(item.getName(), AllItemsFromUser.getFirst().getName());
        assertEquals(item.getDescription(), AllItemsFromUser.getFirst().getDescription());
        assertEquals(item.getAvailable(), AllItemsFromUser.getFirst().getAvailable());
        verify(jpaItemRepository, times(1)).findAllByOwnerId(1L);
    }

    @Test
    void searchAvailableItemsByText() {
        when(jpaItemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase
                        ("Молоток", "Молоток")).thenReturn(List.of(item));

        List<ItemDto> searchAvailableItemsByText = itemServiceImpl.searchAvailableItemsByText("Молоток");
        List<ItemDto> searchAvailableItemsByTextNull = itemServiceImpl.searchAvailableItemsByText("");

        assertTrue(searchAvailableItemsByTextNull.isEmpty());
        assertNotNull(searchAvailableItemsByText);
        assertEquals(item.getId(), searchAvailableItemsByText.getFirst().getId());
        assertEquals(item.getName(), searchAvailableItemsByText.getFirst().getName());
        assertEquals(item.getDescription(), searchAvailableItemsByText.getFirst().getDescription());
        assertEquals(item.getAvailable(), searchAvailableItemsByText.getFirst().getAvailable());
        verify(jpaItemRepository, times(1))
                .findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase
                        ("Молоток", "Молоток");
    }

    @Test
    void deleteItemById() {
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        itemServiceImpl.deleteItemById(1L, 1L);
        verify(jpaItemRepository, times(1)).delete(item);
    }

    @Test
    void deleteAllItemsByUser() {
        when(userServiceImpl.checkUserExist(1L)).thenReturn(user);
        itemServiceImpl.deleteAllItemsByUser(1L);
        verify(jpaItemRepository, times(1)).deleteAllByOwnerId(1L);
    }

    @Test
    void deleteAllItems() {
        itemServiceImpl.deleteAllItems();
        verify(jpaItemRepository, times(1)).deleteAll();
    }

    @Test
    void checkItemExist() {
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Item getItem = itemServiceImpl.checkItemExist(1L);
        assertNotNull(getItem);
        assertEquals(item.getId(), getItem.getId());
        assertEquals(item.getName(), getItem.getName());
        assertEquals(item.getDescription(), getItem.getDescription());
        assertEquals(item.getAvailable(), getItem.getAvailable());
        verify(jpaItemRepository, times(1)).findById(1L);

        assertThrows(NotFoundException.class, () -> itemServiceImpl.checkItemExist(222L));
    }

    @Test
    void checkUserAuthorizationForItem() {
        assertThrows(ForbiddenException.class, () -> itemServiceImpl.checkUserAuthorizationForItem(999L, item));
    }

    @Test
    void createComment() {
        when(userServiceImpl.checkUserExist(anyLong())).thenReturn(user);
        when(jpaItemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(jpaBookingRepository.checkItemReviewAuthorizationAfterRental(
                anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(Optional.ofNullable(booking));

        when(jpaCommentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoResponse createdCommentDtoResponse = itemServiceImpl
                .createComment(1L, 1L, commentDtoRequest);

        assertNotNull(createdCommentDtoResponse);
        assertEquals(createdCommentDtoResponse.getText(), comment.getText());
        verify(jpaCommentRepository, times(1)).save(any(Comment.class));


    }

    @Test
    void checkAuthorAuthorizationForItem() {
        when(jpaBookingRepository.checkItemReviewAuthorizationAfterRental(
                anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(booking));

        Booking checkBooking = itemServiceImpl.checkAuthorAuthorizationForItem(1L, 1L);
        assertNotNull(checkBooking);
        assertEquals(checkBooking.getId(), 1L);
        assertEquals(checkBooking.getStart(), booking.getStart());
        assertEquals(checkBooking.getEnd(), booking.getEnd());
        assertEquals(checkBooking.getItem(), booking.getItem());
        assertEquals(checkBooking.getBooker(), booking.getBooker());
        assertEquals(checkBooking.getStatus(), booking.getStatus());

        verify(jpaBookingRepository, times(1)).checkItemReviewAuthorizationAfterRental(
                anyLong(), anyLong(), any(LocalDateTime.class));

    }

    @Test
    void checkAuthorAuthorizationForItemThrow() {
        assertThrows(ValidationException.class, () -> itemServiceImpl
                .checkAuthorAuthorizationForItem(222L, 222L));
    }
}
