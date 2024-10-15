package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShareItAppServerTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Проверяет, что контекст приложения загружается без ошибок.
     * Также проверяет, что основной класс приложения присутствует в контексте.
     */
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("shareItAppServer")).isTrue();
    }
}