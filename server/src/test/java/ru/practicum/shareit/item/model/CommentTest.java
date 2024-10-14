package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class CommentTest {

    @Test
    void testEquals_SameObject() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item()) // Используйте правильную инициализацию Item
                .author(new User()) // Используйте правильную инициализацию User
                .created(LocalDateTime.now())
                .build();

        // Тестируем равенство с самим собой
        assertThat(comment.equals(comment), is(true));
    }

    @Test
    void testEquals_Null() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        // Тестируем сравнение с null
        assertThat(comment.equals(null), is(false));
    }

    @Test
    void testEquals_DifferentClass() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        // Тестируем сравнение с объектом другого класса
        assertThat(comment.equals("Not a Comment"), is(false));
    }

    @Test
    void testEquals_SameFields() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(comment1.getCreated())
                .build();

        // Тестируем равенство объектов с одинаковыми полями
        assertThat(comment1.equals(comment2), is(true));
    }

    @Test
    void testEquals_DifferentFields() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L) // Разный ID
                .text("Awesome item!") // Разный текст
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        // Тестируем неравенство объектов с различными полями
        assertThat(comment1.equals(comment2), is(false));
    }

    @Test
    void testHashCode_SameFields() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(comment1.getCreated())
                .build();

        // Хеш-коды должны быть одинаковыми для равных объектов
        assertThat(comment1.hashCode(), is(comment2.hashCode()));
    }

    @Test
    void testHashCode_DifferentId() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L) // Разный ID
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(comment1.getCreated())
                .build();

        // Хеш-коды должны различаться из-за разных ID
        assertThat(comment1.hashCode(), is(not(comment2.hashCode())));
    }

    @Test
    void testHashCode_DifferentText() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .text("Awesome item!") // Разный текст
                .item(new Item())
                .author(new User())
                .created(comment1.getCreated())
                .build();

        // Хеш-коды должны различаться из-за разных текстов
        assertThat(comment1.hashCode(), is(not(comment2.hashCode())));
    }

    @Test
    void testHashCode_DifferentCreated() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now().plusDays(1)) // Разное время создания
                .build();

        // Хеш-коды должны различаться из-за разных времён создания
        assertThat(comment1.hashCode(), is(not(comment2.hashCode())));
    }
}
