package ru.practicum.shareit.error.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ErrorResponseTest {

    @Test
    void testErrorResponseConstructorAndGetters() {
        // Данные для теста
        String error = "Не найдено";
        String description = "Запрашиваемый ресурс не найден";

        // Создание экземпляра ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse(error, description);

        // Проверка, что значения полей установлены корректно
        assertThat(errorResponse.getError(), is(error));
        assertThat(errorResponse.getDescription(), is(description));
    }

    @Test
    void testErrorResponseNoArgsConstructor() {
        // Создание экземпляра ErrorResponse с помощью конструктора без аргументов
        ErrorResponse errorResponse = new ErrorResponse();

        // Проверка, что поля инициализированы значением null
        assertThat(errorResponse.getError(), is((String) null));
        assertThat(errorResponse.getDescription(), is((String) null));
    }
}
