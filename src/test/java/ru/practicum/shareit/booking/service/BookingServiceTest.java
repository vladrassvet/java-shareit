package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    private User owner;
    private BookingDtoRequest bookingDtoRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("alex@mail.ru")
                .name("alex")
                .build();
        userRepository.save(user);
        owner = User.builder()
                .email("alexs@mail.ru")
                .name("alexs")
                .build();
        userRepository.save(owner);
        itemRequest = ItemRequest.builder()
                .description("запрос")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest);
        item = Item.builder()
                .name("стул")
                .description("удобный стул")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();
        itemRepository.save(item);
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void addBooking() {
        Booking booking1 = bookingService.addBooking(user.getId(), bookingDtoRequest);
        assertEquals(item.getName(), booking1.getItem().getName());
        assertEquals(owner, booking1.getItem().getOwner());
    }

    @Test
    void getStatus() {
        Booking booking1 = bookingService.addBooking(user.getId(), bookingDtoRequest);
        Booking bookingGetStatus = bookingService.getStatus(booking1.getId(), owner.getId(), true);
        assertTrue(bookingGetStatus != null);
        assertEquals(bookingDtoRequest.getStart(), bookingGetStatus.getStart());
    }

    @Test
    void getBooking() {
        Booking addBooking = bookingService.addBooking(user.getId(), bookingDtoRequest);
        Booking getBooking = bookingService.getBooking(addBooking.getId(), user.getId());
        assertTrue(getBooking != null);
        assertEquals(bookingDtoRequest.getStart(), getBooking.getStart());
    }

    @Test
    void getUserBookings() {
        Booking addBooking = bookingService.addBooking(user.getId(), bookingDtoRequest);
        addBooking.setStatus(BookingStatus.APPROVED);
        bookingService.getStatus(addBooking.getId(), owner.getId(), true);
        List<Booking> bookings = bookingService.getUserBookings(user.getId(), "ALL", 0, 5);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void getUserItems() {
        bookingService.addBooking(user.getId(), bookingDtoRequest);
        List<Booking> bookings = bookingService.getUserItems(owner.getId(), "ALL", 0, 5);
        assertEquals(1, bookings.size());
        assertEquals(owner, bookings.get(0).getItem().getOwner());
    }
}
