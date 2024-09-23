package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

/**
 * Mapper для преобразования сущностей комментариев в DTO.
 */
public class CommentMapper {

    /**
     * Преобразует сущность комментария в DTO.
     *
     * @param comment комментарий, который необходимо преобразовать
     * @return {@link CommentDtoResponse} с данными комментария
     */
    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
