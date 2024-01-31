package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertEquals(comment.getId(), commentDto.getId(), "некорректно отработал Mapper");
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName(), "некорректно отработал Mapper");
        assertEquals(comment.getText(), commentDto.getText(), "некорректно отработал Mapper");
    }

    @Test
    void toComment() {
        User user = new User();
        Item item = new Item();
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("text")
                .build();
        Comment comment = CommentMapper.toComment(commentDtoInput, item, user);
        assertEquals(commentDtoInput.getText(), comment.getText(), "некорректно отработал Mapper");
        assertEquals(user, comment.getAuthor(), "некорректно отработал Mapper");
        assertEquals(item, comment.getItem(), "некорректно отработал Mapper");
    }
}
