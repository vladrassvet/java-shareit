package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private ItemRequest request;
    private User requestor;

    private Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("alex@mail.ru")
                .name("alex")
                .build();
        userRepository.save(user);

        requestor = User.builder()
                .email("r@mail.ru")
                .name("r")
                .build();
        userRepository.save(requestor);
        request = ItemRequest.builder()
                .requestor(requestor)
                .created(LocalDateTime.now())
                .description("description")
                .build();
        itemRequestRepository.save(request);
        item = Item.builder()
                .name("вещь")
                .description("новая вещь")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        itemRepository.save(item);
        comment = Comment.builder()
                .item(item)
                .text("text")
                .author(user)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    @Test
    void findAllByItem() {
        List<Comment> comments = commentRepository.findAllByItem(item);
        assertEquals(1, comments.size());
    }

    @AfterEach
    void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}