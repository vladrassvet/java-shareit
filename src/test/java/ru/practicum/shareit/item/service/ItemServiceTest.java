package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private ItemDto itemDto;
    private User user;
    private User requestor;

    private ItemRequest itemRequest;

    private Item item;

    @BeforeEach
    void setUp() {
        requestor = User.builder()
                .email("as@mail.ru")
                .name("as")
                .build();
        userRepository.save(requestor);
        user = User.builder()
                .email("alex@yandex.ru")
                .name("alex")
                .build();
        userRepository.save(user);
        itemRequest = ItemRequest.builder()
                .description("запрос")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
        itemRequestRepository.save(itemRequest);
        itemDto = ItemDto.builder()
                .name("дрель")
                .description("супер дрель")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void addItem() {
        Item item1 = itemService.addItem(user.getId(), itemDto);
        assertEquals(user, item1.getOwner());
        assertEquals(requestor, item1.getRequest().getRequestor());
    }

    @Test
    void updateItem() {
        Item item1 = itemService.addItem(user.getId(), itemDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("дрель отремонтированная")
                .description("супер дрель")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
        Item updateItem = itemService.updateItem(item1.getId(), user.getId(), itemDto1);
        assertEquals(itemDto1.getName(), updateItem.getName());
        assertEquals(item1.getId(), updateItem.getId());
    }

    @Test
    void getItem() {
        Item item1 = itemService.addItem(user.getId(), itemDto);
        ItemWithCommentsAndBookings newItem = itemService.getItem(item1.getId(), user.getId());
        assertTrue(newItem.getComments().isEmpty());
        assertTrue(newItem.getLastBooking() == null);

    }

    @Test
    void getUserItems() {
        itemService.addItem(user.getId(), itemDto);
        List<ItemWithCommentsAndBookings> list = itemService.getUserItems(user.getId(), 0, 5);
        assertEquals(1, list.size());
    }

    @Test
    void searchItem() {
        itemService.addItem(user.getId(), itemDto);
        List<Item> items = itemService.searchItem("супер", 0, 5);
        assertEquals(itemDto.getName(), items.get(0).getName());
    }

    @Test
    void addComment() {
        Item item1 = itemService.addItem(user.getId(), itemDto);
        Booking booking = Booking.builder()
                .item(item1)
                .bookingUser(requestor)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("text")
                .build();
        Comment comment = itemService.addComment(requestor.getId(), item1.getId(), commentDtoInput);
        assertEquals("text", comment.getText());
    }
}
