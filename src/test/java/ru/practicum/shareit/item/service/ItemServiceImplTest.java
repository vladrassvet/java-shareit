package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exсeption.NotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemDto itemDto;
    private User user;

    private ItemRequest itemRequest;

    private Item item;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("супер дрель")
                .available(true)
                .requestId(1L)
                .build();
        user = User.builder()
                .id(1L)
                .email("alex@yandex.ru")
                .name("alex")
                .build();
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
    }

    @Test
    void addItem_whenUserAndItemFound_thenReturnItem() {
        Item actualItem = ItemMapper.toItem(user, itemDto, itemRequest);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(ItemMapper.toItem(user, itemDto, itemRequest))).thenReturn(actualItem);

        Item itemReturn = itemService.addItem(user.getId(), itemDto);
        assertEquals(user.getId(), itemReturn.getOwner().getId(), "некорректно отработал метод");
        assertEquals(itemRequest.getCreated(), itemReturn.getRequest().getCreated(), "метод отработал некорректно");
    }

    @Test
    void addItem_whenUserNotFound_thenReturnThrows() {
        long userId = 222L;
        User notValidUser = new User();
        ItemDto itemDto1 = ItemDto.builder()
                .id(100L)
                .name("дрель")
                .description("супер дрель")
                .available(true)
                .requestId(1L)
                .build();
        ItemRequest itemRequest = new ItemRequest();

        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addItem(userId, itemDto1));
        verify(itemRepository, never()).save(ItemMapper.toItem(notValidUser, itemDto1, itemRequest));
    }

    @Test
    void addItem_whenItemNotValid_thenReturnThrows() {
        User user1 = new User();
        user1.setId(1L);
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);

        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestRepository.findById(itemRequest1.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addItem(user1.getId(), itemDto));
        verify(itemRepository, never()).save(ItemMapper.toItem(user1, itemDto, itemRequest1));
    }

    @Test
    void updateItem_whenEntityValid_thenReturnUpdateItem() {
        Item item = ItemMapper.toItem(user, itemDto, itemRequest);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(user);

        Item updateItem = itemService.updateItem(item.getId(), user.getId(), itemDto);
        assertEquals(user.getEmail(), updateItem.getOwner().getEmail(), "некорректно отработал метод");
        verify(itemRepository, times(1)).save(ItemMapper.toItemWithId(item.getId(), user, itemDto));
    }

    @Test
    void updateItem_whenItemNotValid_thenReturnThrows() {
        Item notValidItem = new Item();
        notValidItem.setId(1L);
        long userId = 1L;
        User user1 = new User();

        when(itemRepository.findById(notValidItem.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(notValidItem.getId(), userId, itemDto));
        verify(itemRepository, never()).save(ItemMapper.toItemWithId(notValidItem.getId(), user1, itemDto));
    }

    @Test
    void updateItem_whenUserNotValid_thenReturnThrows() {
        Item item = ItemMapper.toItem(user, itemDto, itemRequest);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(item.getId(), user.getId(), itemDto));
        verify(itemRepository, never()).save(ItemMapper.toItemWithId(item.getId(), user, itemDto));
    }

    @Test
    void getItem_whenItemFound_thenReturnItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemWithCommentsAndBookings item1 = itemService.getItem(item.getId(), user.getId());
        assertNotNull(item1);
        assertEquals(item.getDescription(), item1.getDescription(), "некорректно отработал метод");
        assertTrue(item1.getName().equals("стул"));

    }

    @Test
    void getItem_whenItemNotFound_thenReturnThrows() {
        when(itemRepository.findById(item.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getItem(item.getId(), user.getId()));
    }

    @Test
    void getUserItems_whenInputValueValid_thenReturnItemWithCommentsAndBookings() {
        List<ItemWithCommentsAndBookings> list = new ArrayList<>();
        BookingShort lastBooking = BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .build();
        BookingShort nextBooking = BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .build();
        ItemWithCommentsAndBookings item1 = ItemWithCommentsAndBookings.builder()
                .id(1L)
                .name("!!!")
                .description("QWERTY")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(new ArrayList<>())
                .build();
        list.add(item1);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Pageable pageable = PageRequest.of(15 / 10, 10);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findAllByOwnerId(user.getId(), pageable)).thenReturn(itemList);

        List<ItemWithCommentsAndBookings> item2 = itemService.getUserItems(user.getId(), 15, 10);
        assertFalse(item2.isEmpty());
        assertEquals(1, item2.size(), "некорректно отработал метод");
        assertEquals("удобный стул", item2.get(0).getDescription(), "некорректно отработал метод");
    }

    @Test
    void getUserItems_whenInputValueNotValid_thenReturnTrows() {
        Integer from = 5;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from / size, size);

        when(userService.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getUserItems(user.getId(), from, size));
        verify(itemRepository, never()).findAllByOwnerId(user.getId(), pageable);
    }

    @Test
    void searchItem_whenInputValidValue_thenReturnPaginationValue() {
        String text = "удобный";
        Integer from = 1;
        Integer size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Pageable pageable = PageRequest.of(from / size, size);

        when(itemRepository.searchWithPagination(text, pageable)).thenReturn(itemList);

        List<Item> actualItemList = itemService.searchItem(text, from, size);
        assertFalse(actualItemList.isEmpty());
        assertEquals(1, actualItemList.size(), "некорректно отработал метод");
        assertEquals(item, actualItemList.get(0), "некорректно отработал метод");
    }

    @Test
    void addComment_whenInputValue_thenReturnSaveComment() {
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("отличная вещь, очень удобная в хозяйстве")
                .build();
        User user1 = User.builder()
                .id(2L)
                .email("alexs@yandex.ru")
                .name("alexs")
                .build();
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .bookingUser(user1)
                .start(dateTime)
                .end(dateTime.plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
        Comment comment = CommentMapper.toComment(commentDtoInput, item, user);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findFirstByBookingUserIdAndItemIdAndEndIsBeforeOrderByEndDesc(any(Long.class),
                any(Long.class), any(LocalDateTime.class))).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            comment.setId(1L);
            return comment;
        });

        Comment newComment = itemService.addComment(user.getId(), item.getId(), commentDtoInput);
        assertFalse(newComment == null);
        assertEquals(commentDtoInput.getText(), newComment.getText(), "некорректно отработал метод");
    }

    @Test
    void addComment_whenInputNotValidUserId_thenReturnThrows() {
        long itemId = 1L;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("отличная вещь, очень удобная в хозяйстве")
                .build();

        when(userService.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), itemId, commentDtoInput));
    }

    @Test
    void addComment_whenInputNotValidItemId_thenReturnThrows() {
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("отличная вещь, очень удобная в хозяйстве")
                .build();

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDtoInput));
    }
}
