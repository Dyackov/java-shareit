package ru.practicum.shareit.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Класс ErrorResponse представляет собой модель ответа об ошибке, возвращаемого клиенту.
 * <p>
 * Этот класс используется для предоставления клиентам информации об ошибках, которые произошли на сервере,
 * таких как сообщения об ошибках и дополнительное описание проблемы.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    /**
     * Краткое описание типа ошибки.
     * Например, это может быть "Не найдено" или "Ошибка валидации".
     */
    String error;
    /**
     * Детальное описание ошибки.
     * Может содержать дополнительные сведения, такие как сообщение об ошибке,
     * причина ошибки или конкретные поля, вызвавшие ошибку.
     */
    String description;

}