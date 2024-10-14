package ru.practicum.shareit.error.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ErrorStateResponseTest {

    @Test
    void testErrorStateResponseConstructorAndGetter() {
        // Данные для теста
        String expectedError = "Ошибка аутентификации";

        // Создание экземпляра ErrorStateResponse
        ErrorStateResponse errorStateResponse = new ErrorStateResponse(expectedError);

        // Проверка, что значение поля error установлено корректно
        assertThat(errorStateResponse.error(), is(expectedError));
    }
}
