package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private Item item;
    private User user;
    private ItemRequest itemRequest;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("запрос")
                .created(LocalDateTime.now())
                .requestor(new User())
                .build();
        item = Item.builder()
                .id(1L)
                .name("стул")
                .description("удобный стул")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        user = User.builder()
                .id(1L)
                .email("alex@yandex.ru")
                .name("alex")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("рубанок")
                .description("для обработки дерева")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertEquals(itemDto.getRequestId(), item.getRequest().getId(), "некорректно работает Mapper");
    }

    @Test
    void toItem() {
        Item item1 = ItemMapper.toItem(user, itemDto, itemRequest);
        assertEquals(user, item1.getOwner(), "некорректно отработал Mapper");
    }

    @Test
    void toItemWithId() {
        Item item1 = ItemMapper.toItemWithId(item.getId(), user, itemDto);
        assertEquals(item1.getName(), itemDto.getName(), "некорректно отработал Mapper");
    }

    @Test
    void toItemWithTime() {
        List<CommentDto> list = new ArrayList<>();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("alex")
                .created(LocalDateTime.now())
                .build();
        list.add(commentDto);
        BookingShort last = BookingShort.builder()
                .id(1L)
                .bookerId(1L).build();
        BookingShort next = BookingShort.builder()
                .id(2L).bookerId(2L).build();
        ItemWithCommentsAndBookings item1 = ItemMapper.toItemWithTime(item, last, next, list);
        assertEquals(item1.getDescription(), item.getDescription(), "некорректно отработал Mapper");
        assertEquals(item1.getLastBooking(), last, "некорректно отработал Mapper");
        assertEquals(item1.getComments().get(0), commentDto, "некорректно отработал Mapper");
    }
}