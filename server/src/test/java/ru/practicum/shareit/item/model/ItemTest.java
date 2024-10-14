package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class ItemTest {

    @Test
    void testEquals_SameObject() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Тестируем равенство с самим собой
        assertThat(item.equals(item), is(true));
    }

    @Test
    void testEquals_Null() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Тестируем сравнение с null
        assertThat(item.equals(null), is(false));
    }

    @Test
    void testEquals_DifferentClass() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Тестируем сравнение с объектом другого класса
        assertThat(item.equals("Not an Item"), is(false));
    }

    @Test
    void testEquals_SameFields() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Тестируем равенство объектов с одинаковыми полями
        assertThat(item1.equals(item2), is(true));
    }

    @Test
    void testEquals_DifferentFields() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Another Item")
                .description("Another Description")
                .available(false)
                .owner(User.builder().id(2L).name("Another Owner").build())
                .request(ItemRequest.builder().id(2L).description("Another Request").build())
                .build();

        // Тестируем неравенство объектов с различными полями
        assertThat(item1.equals(item2), is(false));
    }

    @Test
    void testHashCode_SameFields() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Хеш-коды должны быть одинаковыми для равных объектов
        assertThat(item1.hashCode(), is(item2.hashCode()));
    }

    @Test
    void testHashCode_DifferentId() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(2L) // Разный ID
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Хеш-коды должны различаться из-за разных ID
        assertThat(item1.hashCode(), is(not(item2.hashCode())));
    }

    @Test
    void testHashCode_DifferentName() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Another Item") // Разное название
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Хеш-коды должны различаться из-за разных названий
        assertThat(item1.hashCode(), is(not(item2.hashCode())));
    }

    @Test
    void testHashCode_DifferentDescription() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Another Description") // Разное описание
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Хеш-коды должны различаться из-за разных описаний
        assertThat(item1.hashCode(), is(not(item2.hashCode())));
    }

    @Test
    void testHashCode_DifferentAvailable() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true) // Доступно
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(false) // Недоступно
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Хеш-коды должны различаться из-за разных доступностей
        assertThat(item1.hashCode(), is(not(item2.hashCode())));
    }

    @Test
    void testHashCode_DifferentOwner() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(2L).name("Another Owner").build()) // Разный владелец
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        // Хеш-коды должны различаться из-за разных владельцев
        assertThat(item1.hashCode(), is(not(item2.hashCode())));
    }

    @Test
    void testHashCode_DifferentRequest() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(1L).description("Request").build())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(User.builder().id(1L).name("Owner").build())
                .request(ItemRequest.builder().id(2L).description("Another Request").build()) // Разный запрос
                .build();

        // Хеш-коды должны различаться из-за разных запросов
        assertThat(item1.hashCode(), is(not(item2.hashCode())));
    }
}
