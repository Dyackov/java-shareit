package ru.practicum.shareit.error.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ErrorStateResponseTest {

    @Test
    void testErrorStateResponseConstructorAndGetter() {
        String expectedError = "Ошибка аутентификации";

        ErrorStateResponse errorStateResponse = new ErrorStateResponse(expectedError);

        assertThat(errorStateResponse.error(), is(expectedError));
    }
}