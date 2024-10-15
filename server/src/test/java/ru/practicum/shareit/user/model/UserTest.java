package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UserTest {

    @Test
    void testUserBuilderAndGetters() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user.getId(), is(1L));
        assertThat(user.getName(), is("John Doe"));
        assertThat(user.getEmail(), is("john.doe@example.com"));
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user1, is(user2));
        assertThat(user1.hashCode(), is(user2.hashCode()));
    }

    @Test
    void testEquals_SameObject() {
        // Сравнение объекта с самим собой
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user.equals(user), is(true));
    }

    @Test
    void testEquals_NullObject() {
        // Сравнение с null
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user.equals(null), is(false));
    }

    @Test
    void testEquals_DifferentClass() {
        // Сравнение с объектом другого класса
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        String otherObject = "not a user";
        assertThat(user.equals(otherObject), is(false));
    }

    @Test
    void testEquals_DifferentId() {
        // Сравнение объектов с разными id
        User user1 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user1.equals(user2), is(false));
    }

    @Test
    void testEquals_DifferentName() {
        // Сравнение объектов с разными именами
        User user1 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("Jane Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user1.equals(user2), is(false));
    }

    @Test
    void testEquals_DifferentEmail() {
        // Сравнение объектов с разными email
        User user1 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("jane.doe@example.com")
                .build();

        assertThat(user1.equals(user2), is(false));
    }

    @Test
    void testEquals_EqualObjects() {
        // Сравнение объектов с одинаковыми значениями всех полей
        User user1 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user1.equals(user2), is(true));
    }

    @Test
    void testSetters() {
        User user = User.builder().build();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        assertThat(user.getId(), is(1L));
        assertThat(user.getName(), is("John Doe"));
        assertThat(user.getEmail(), is("john.doe@example.com"));
    }
}
