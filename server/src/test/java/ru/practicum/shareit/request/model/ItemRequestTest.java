package ru.practicum.shareit.request.model;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class ItemRequestTest {

    @Test
    void testEquals_SameObject() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        assertThat(itemRequest.equals(itemRequest), is(true));
    }

    @Test
    void testEquals_Null() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(LocalDateTime.now())
                .build();

        assertThat(itemRequest.equals(null), is(false));
    }

    @Test
    void testEquals_DifferentClass() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(LocalDateTime.now())
                .build();

        assertThat(itemRequest.equals("Not an ItemRequest"), is(false));
    }

    @Test
    void testEquals_SameFields() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        assertThat(itemRequest1.equals(itemRequest2), is(true));
    }

    @Test
    void testEquals_DifferentFields() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Another Description")
                .requestor(User.builder().id(2L).name("Jane").build())
                .created(now)
                .build();

        assertThat(itemRequest1.equals(itemRequest2), is(false));
    }

    @Test
    void testHashCode_SameFields() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        assertThat(itemRequest1.hashCode(), is(itemRequest2.hashCode()));
    }

    @Test
    void testHashCode_DifferentId() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        assertThat(itemRequest1.hashCode(), is(not(itemRequest2.hashCode())));
    }

    @Test
    void testHashCode_DifferentDescription() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("Another Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        assertThat(itemRequest1.hashCode(), is(not(itemRequest2.hashCode())));
    }

    @Test
    void testHashCode_DifferentRequestor() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(2L).name("Jane").build()) // Разный запросчик
                .created(now)
                .build();

        assertThat(itemRequest1.hashCode(), is(not(itemRequest2.hashCode())));
    }

    @Test
    void testHashCode_DifferentCreated() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(User.builder().id(1L).name("John").build())
                .created(now.plusDays(1))
                .build();

        assertThat(itemRequest1.hashCode(), is(not(itemRequest2.hashCode())));
    }
}