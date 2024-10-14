package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.JpaItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final JpaItemRepository jpaItemRepository;
    private final JpaBookingRepository jpaBookingRepository;
    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final JpaUserRepository jpaUserRepository;
    private final ItemServiceImpl itemServiceImpl;

    @Test
    void createItem_shouldCreateItemSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        User requestorUser = User.builder()
                .name("John Request")
                .email("john.request@example.com")
                .build();
        requestorUser = jpaUserRepository.save(requestorUser);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Молоток")
                .requestor(requestorUser)
                .created(LocalDateTime.now())
                .build();
        itemRequest = jpaItemRequestRepository.save(itemRequest);

        ItemDto createdItemDto = ItemDto.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        createdItemDto = itemServiceImpl.createItem(user.getId(), createdItemDto);

        assertThat(createdItemDto).isNotNull();
        assertThat(createdItemDto.getId()).isNotNull();
        assertThat(createdItemDto.getName()).isEqualTo("Молоток");
        assertThat(createdItemDto.getDescription()).isEqualTo("Строительный молоток");
        assertThat(createdItemDto.getAvailable()).isEqualTo(true);
        assertThat(createdItemDto.getRequestId()).isEqualTo(itemRequest.getId());

        Item savedItem = jpaItemRepository.findById(createdItemDto.getId()).orElse(null);
        assertThat(savedItem).isNotNull();
        assertThat(savedItem != null ? savedItem.getName() : null).isEqualTo("Молоток");
        assertThat(savedItem != null ? savedItem.getDescription() : null).isEqualTo("Строительный молоток");
        assertThat(savedItem != null ? savedItem.getAvailable() : null).isEqualTo(true);
        assertThat(savedItem != null ? savedItem.getRequest() : null).isEqualTo(itemRequest);
    }

    @Test
    void updateItem_shouldUpdateItemDtoSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);


        ItemDto newItemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Крестовая")
                .available(false)
                .build();
        newItemDto = itemServiceImpl.createItem(user.getId(), newItemDto);

        ItemDto updatedItemDto = itemServiceImpl.updateItem(user.getId(), item.getId(), newItemDto);
        assertThat(updatedItemDto).isNotNull();
        assertThat(updatedItemDto.getId()).isEqualTo(item.getId());
        assertThat(updatedItemDto.getName()).isEqualTo("Отвертка");
        assertThat(updatedItemDto.getDescription()).isEqualTo("Крестовая");
        assertThat(updatedItemDto.getAvailable()).isEqualTo(false);

        Item savedItem = jpaItemRepository.findById(updatedItemDto.getId()).orElse(null);
        assertThat(savedItem).isNotNull();
        assertThat(savedItem != null ? savedItem.getName() : null).isEqualTo("Отвертка");
        assertThat(savedItem != null ? savedItem.getDescription() : null).isEqualTo("Крестовая");
        assertThat(savedItem != null ? savedItem.getAvailable() : null).isEqualTo(false);
        assertThat(savedItem != null ? savedItem.getOwner() : null).isEqualTo(user);
    }

    @Test
    void getItemById_shouldGetItemByIdSuccessfully_ForOwner() {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        ItemDtoBooking getItemDto = itemServiceImpl.getItemById(owner.getId(), item.getId());

        assertThat(getItemDto).isNotNull();
        assertThat(getItemDto.getId()).isEqualTo(item.getId());
        assertThat(getItemDto.getName()).isEqualTo("Молоток");
        assertThat(getItemDto.getDescription()).isEqualTo("Строительный молоток");
        assertThat(getItemDto.getAvailable()).isEqualTo(true);

        assertThat(getItemDto.getLastBooking()).isNull();
        assertThat(getItemDto.getNextBooking()).isNull();
    }

    @Test
    void getItemById_shouldNotIncludeBookings_ForNonOwner() {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        User nonOwner = User.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();
        nonOwner = jpaUserRepository.save(nonOwner);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        ItemDtoBooking getItemDto = itemServiceImpl.getItemById(nonOwner.getId(), item.getId());

        assertThat(getItemDto).isNotNull();
        assertThat(getItemDto.getId()).isEqualTo(item.getId());
        assertThat(getItemDto.getName()).isEqualTo("Молоток");

        assertThat(getItemDto.getLastBooking()).isNull();
        assertThat(getItemDto.getNextBooking()).isNull();
    }

    @Test
    void getItemById_shouldIncludeBookings_ForOwnerWithBookings() {
        User owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(owner)
                .build();
        item = jpaItemRepository.save(item);

        Booking lastBooking = Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .booker(owner)
                .build();
        jpaBookingRepository.save(lastBooking);

        Booking nextBooking = Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(10))
                .booker(owner)
                .build();
        jpaBookingRepository.save(nextBooking);

        ItemDtoBooking getItemDto = itemServiceImpl.getItemById(owner.getId(), item.getId());

        assertThat(getItemDto).isNotNull();
        assertThat(getItemDto.getLastBooking()).isNotNull();
        assertThat(getItemDto.getNextBooking()).isNotNull();
    }

    @Test
    void getItemById_shouldGetItemByIdSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);

        ItemDtoBooking getItemDto = itemServiceImpl.getItemById(user.getId(), item.getId());

        assertThat(getItemDto).isNotNull();
        assertThat(getItemDto).isNotNull();
        assertThat(getItemDto.getId()).isEqualTo(item.getId());
        assertThat(getItemDto.getName()).isEqualTo("Молоток");
        assertThat(getItemDto.getDescription()).isEqualTo("Строительный молоток");
        assertThat(getItemDto.getAvailable()).isEqualTo(true);

        Item savedItem = jpaItemRepository.findById(getItemDto.getId()).orElse(null);
        assertThat(savedItem).isNotNull();
        assertThat(savedItem != null ? savedItem.getName() : null).isEqualTo("Молоток");
        assertThat(savedItem != null ? savedItem.getDescription() : null).isEqualTo("Строительный молоток");
        assertThat(savedItem != null ? savedItem.getAvailable() : null).isEqualTo(true);
        assertThat(savedItem != null ? savedItem.getOwner() : null).isEqualTo(user);

    }

    @Test
    void getAllItemsFromUser_shouldGetAllItemsFromUserSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item1 = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item1);

        Item item2 = Item.builder()
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item2);

        Item item3 = Item.builder()
                .name("Лопата")
                .description("Совковая")
                .available(false)
                .owner(user)
                .build();
        jpaItemRepository.save(item3);

        List<ItemDtoBooking> items = itemServiceImpl.getAllItemsFromUser(user.getId());

        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(3);
        assertThat(items.getFirst().getName()).isEqualTo("Молоток");
        assertThat(items.get(0).getDescription()).isEqualTo("Строительный молоток");
        assertThat(items.get(0).getAvailable()).isEqualTo(true);

        assertThat(items.get(1).getName()).isEqualTo("Отвёртка");
        assertThat(items.get(1).getDescription()).isEqualTo("Крестовая");
        assertThat(items.get(1).getAvailable()).isEqualTo(true);

        assertThat(items.get(2).getName()).isEqualTo("Лопата");
        assertThat(items.get(2).getDescription()).isEqualTo("Совковая");
        assertThat(items.get(2).getAvailable()).isEqualTo(false);
    }

    @Test
    void searchAvailableItemsByText_ShouldReturnListNull() {
        List<ItemDto> itemsTextEmpty = itemServiceImpl.searchAvailableItemsByText(null);
        assertThat(itemsTextEmpty).isNotNull();
        assertThat(itemsTextEmpty.size()).isEqualTo(0);
    }

    @Test
    void searchAvailableItemsByText_ShouldReturnListEmpty() {
        List<ItemDto> itemsTextEmpty = itemServiceImpl.searchAvailableItemsByText("");
        assertThat(itemsTextEmpty).isNotNull();
        assertThat(itemsTextEmpty.size()).isEqualTo(0);
    }

    @Test
    void searchAvailableItemsByText_shouldSearchItemsByTextSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item1 = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item1);

        List<ItemDto> itemsTextNull = itemServiceImpl.searchAvailableItemsByText("Молоток");
        assertThat(itemsTextNull).isNotNull();
        assertThat(itemsTextNull.size()).isEqualTo(1);
        assertThat(itemsTextNull.getFirst().getName()).isEqualTo("Молоток");
    }

    @Test
    void deleteItemById_shouldDeleteItemSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item);

        assertThat(jpaItemRepository.findById(item.getId())).isPresent();

        itemServiceImpl.deleteItemById(user.getId(), item.getId());
        List<ItemDtoBooking> items = itemServiceImpl.getAllItemsFromUser(user.getId());
        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(0);

        assertThat(jpaItemRepository.findById(item.getId())).isNotPresent();
    }

    @Test
    void deleteAllItemsByUser_shouldDeleteAllItemsByUserSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item1 = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item1);

        Item item2 = Item.builder()
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item2);

        assertThat(jpaItemRepository.findById(item1.getId())).isPresent();
        assertThat(jpaItemRepository.findById(item2.getId())).isPresent();

        itemServiceImpl.deleteAllItemsByUser(user.getId());
        List<ItemDtoBooking> items = itemServiceImpl.getAllItemsFromUser(user.getId());
        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(0);
        assertThat(jpaItemRepository.findById(item1.getId())).isNotPresent();
        assertThat(jpaItemRepository.findById(item2.getId())).isNotPresent();
    }

    @Test
    void deleteAllItems_shouldDeleteAllItemsSuccessfully() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item1 = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item1);

        Item item2 = Item.builder()
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .owner(user)
                .build();
        jpaItemRepository.save(item2);

        assertThat(jpaItemRepository.findById(item1.getId())).isPresent();
        assertThat(jpaItemRepository.findById(item2.getId())).isPresent();

        itemServiceImpl.deleteAllItems();

        List<ItemDtoBooking> items = itemServiceImpl.getAllItemsFromUser(user.getId());
        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(0);
        assertThat(jpaItemRepository.findById(item1.getId())).isNotPresent();
        assertThat(jpaItemRepository.findById(item2.getId())).isNotPresent();
        assertThat(jpaItemRepository.findAll()).isEqualTo(List.of());
    }

    @Test
    void checkItemExist_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        long itemId = 999L;
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                itemServiceImpl.checkItemExist(itemId)
        );
        assertEquals("Вещи с ID: " + itemId + " не существует.", thrown.getMessage());
    }

    @Test
    void checkItemExist_ShouldReturnUser_WhenUserExists() {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = jpaUserRepository.save(user);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user)
                .build();
        item = jpaItemRepository.save(item);

        Item checkItem = itemServiceImpl.checkItemExist(item.getId());

        assertThat(checkItem).isEqualTo(item);
        assertThat(checkItem.getName()).isEqualTo("Молоток");
        assertThat(checkItem.getDescription()).isEqualTo("Строительный молоток");
        assertThat(checkItem.getAvailable()).isEqualTo(true);
        assertThat(checkItem.getOwner()).isEqualTo(user);
    }

    @Test
    void checkUserAuthorizationForItem_ShouldThrowForbiddenException() {
        User user1 = User.builder()
                .name("John")
                .email("john@example.com")
                .build();
        user1 = jpaUserRepository.save(user1);

        User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user2 = jpaUserRepository.save(user2);

        Item item1 = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user1)
                .build();
        jpaItemRepository.save(item1);

        Item item2 = Item.builder()
                .name("Лопата")
                .description("Совковая")
                .available(true)
                .owner(user2)
                .build();
        item2 = jpaItemRepository.save(item2);

        final Item itemThrow = item2;
        final long user1Id = user1.getId();

        assertThatThrownBy(() -> itemServiceImpl.checkUserAuthorizationForItem(user1Id, itemThrow))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Ошибка авторизации.");
    }

    @Test
    void createComment_shouldCreateCommentSuccessfully() {
        User user1 = User.builder()
                .name("John")
                .email("john@example.com")
                .build();
        user1 = jpaUserRepository.save(user1);

        User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user2 = jpaUserRepository.save(user2);

        Item item = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user1)
                .build();
        item = jpaItemRepository.save(item);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        jpaBookingRepository.save(booking);

        CommentDtoRequest comment = CommentDtoRequest.builder().text("Очень крепкий").build();
        CommentDtoResponse commentDtoResponse = itemServiceImpl.createComment(user2.getId(), item.getId(), comment);

        assertThat(commentDtoResponse).isNotNull();
        assertThat(commentDtoResponse.getText()).isEqualTo("Очень крепкий");
    }

    @Test
    void checkAuthorAuthorizationForItem_ShouldPass_WhenBookerHasValidBooking() {
        User booker = User.builder()
                .name("Booker User")
                .email("booker@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        User owner = User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Test Item")
                .description("Description of Test Item")
                .owner(owner)
                .available(true)
                .build();
        item = jpaItemRepository.save(item);

        Booking booking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        jpaBookingRepository.save(booking);

        Booking validBooking = itemServiceImpl.checkAuthorAuthorizationForItem(booker.getId(), item.getId());
        assertThat(validBooking).isNotNull();
        assertThat(validBooking.getId()).isEqualTo(booking.getId());
    }

    @Test
    void checkAuthorAuthorizationForItem_ShouldThrowValidationException_WhenBookerHasNoValidBooking() {
        User booker = User.builder()
                .name("Booker User")
                .email("booker@example.com")
                .build();
        booker = jpaUserRepository.save(booker);

        User owner = User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build();
        owner = jpaUserRepository.save(owner);

        Item item = Item.builder()
                .name("Test Item")
                .description("Description of Test Item")
                .owner(owner)
                .available(true)
                .build();
        item = jpaItemRepository.save(item);

        final long bookerId = booker.getId();
        final long itemId = item.getId();

        assertThatThrownBy(() -> itemServiceImpl.checkAuthorAuthorizationForItem(bookerId, itemId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Не пройдена авторизация. ID booker: " + booker.getId() + ", ID item: " + item.getId());
    }
}
