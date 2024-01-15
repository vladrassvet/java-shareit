package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();

    }

    public static Comment toComment(CommentDtoInput commentDtoInput, Item item, User user) {
        return Comment.builder()
                .text(commentDtoInput.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }
}
