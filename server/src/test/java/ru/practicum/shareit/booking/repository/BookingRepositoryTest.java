package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
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
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Item item;
    private User user;
    private User booker;
    private User booker1;
    private ItemRequest request;

    private Booking booking;
    private Booking booking1;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .email("r@mail.ru")
                .name("r")
                .build();
        userRepository.save(booker);
        booker1 = User.builder()
                .email("rs@mail.ru")
                .name("rs")
                .build();
        userRepository.save(booker1);
        request = ItemRequest.builder()
                .requestor(booker)
                .created(LocalDateTime.now())
                .description("description")
                .build();
        itemRequestRepository.save(request);
        user = User.builder()
                .email("alex@mail.ru")
                .name("alex")
                .build();
        userRepository.save(user);
        item = Item.builder()
                .name("вещь")
                .description("новая вещь")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        itemRepository.save(item);
        booking = Booking.builder()
                .item(item)
                .bookingUser(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        booking1 = Booking.builder()
                .item(item)
                .bookingUser(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking1);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookingUserOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookingUserOrderByStartDesc(booker, PageRequest.of(0, 5));
        assertFalse(bookings.isEmpty());
        assertEquals(booker, bookings.get(0).getBookingUser());
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart() {
        List<Booking> bookings = bookingRepository.findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(booker,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), PageRequest.of(0, 5));
        assertEquals(1, bookings.size());
        assertFalse(bookings.contains(booking));
    }

    @Test
    void findAllByBookingUserAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookingUserAndStartIsAfterOrderByStartDesc(booker,
                LocalDateTime.now().minusHours(5), PageRequest.of(0, 5));
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookingUserAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookingUserAndStartIsAfterOrderByStartDesc(booker,
                LocalDateTime.now().plusDays(5), PageRequest.of(0, 5));
        assertEquals(0, bookings.size());
    }

    @Test
    void findAllByBookingUserAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookingUserAndStatusEqualsOrderByStartDesc(booker,
                BookingStatus.WAITING, PageRequest.of(0, 1));
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void findAllByItemOwnerOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(user, PageRequest.of(0, 5));
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(user,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), PageRequest.of(0, 5));
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void findAllByItemOwnerAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(user,
                LocalDateTime.now().minusHours(1), PageRequest.of(0, 1));
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void findAllByItemOwnerAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(user,
                LocalDateTime.now().plusDays(3), PageRequest.of(0, 2));
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(user,
                BookingStatus.WAITING, PageRequest.of(0, 1));
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc() {
        Booking booking2 = bookingRepository.findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(item.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertEquals(booking1, booking2);
    }

    @Test
    void findFirstByItemIdAndStartIsAfterOrderByStart() {
        Booking booking2 = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStart(item.getId(),
                LocalDateTime.now().minusHours(4));
        assertEquals(booking, booking2);
    }

    @Test
    void findFirstByBookingUserIdAndItemIdAndEndIsBeforeOrderByEndDesc() {
        Booking booking2 = bookingRepository.findFirstByBookingUserIdAndItemIdAndEndIsBeforeOrderByEndDesc(booker.getId(),
                item.getId(), LocalDateTime.now().plusDays(1));
        assertEquals(booking, booking2);
    }
}
