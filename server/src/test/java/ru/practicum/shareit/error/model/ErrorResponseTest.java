package ru.practicum.shareit.error.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ErrorResponseTest {

    @Test
    void testErrorResponseConstructorAndGetters() {
        String error = "Не найдено";
        String description = "Запрашиваемый ресурс не найден";

        ErrorResponse errorResponse = new ErrorResponse(error, description);

        assertThat(errorResponse.getError(), is(error));
        assertThat(errorResponse.getDescription(), is(description));
    }

    @Test
    void testErrorResponseNoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();

        assertThat(errorResponse.getError(), is((String) null));
        assertThat(errorResponse.getDescription(), is((String) null));
    }
}