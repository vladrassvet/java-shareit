package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    private User owner;
    private BookingDtoRequest request;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("alex@mail.ru")
                .name("alex")
                .build();
        owner = User.builder()
                .id(2L)
                .email("alexs@mail.ru")
                .name("alexs")
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
                .owner(owner)
                .request(itemRequest)
                .build();
        request = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
        booking = BookingMapper.toBooking(request, item, user, BookingStatus.WAITING);
    }

    @Test
    void addBooking_whenInputValueValid_thenReturnSaveBooking() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Booking saveBooking = bookingService.addBooking(user.getId(), request);

        assertEquals(saveBooking.getItem(), item, "Метод работает некорректно");
        assertEquals(saveBooking.getBookingUser(), user, "Метод работает некорректно");
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_UserNotValid_thenReturnTrows() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), request));
    }

    @Test
    void addBooking_ItemNotValid_thenReturnTrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), request));
    }

    @Test
    void getStatus_whenAllInputValueValid_thenReturnBookigg() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Booking actualStatusBooking = bookingService.getStatus(booking.getId(), owner.getId(), true);
        assertEquals(BookingStatus.APPROVED, actualStatusBooking.getStatus(), "Метод отработал некорректно");
        assertEquals(item, actualStatusBooking.getItem(), "Метод отработал некорректно");
    }

    @Test
    void getStatus_whenUserNotValid_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getStatus(booking.getId(), user.getId(), true));
    }

    @Test
    void getStatus_whenBookingNotValid_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getStatus(booking.getId(), user.getId(), true));
    }

    @Test
    void getStatus_whenUserIdEqualsOwner_thenReturnThrows() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> bookingService.getStatus(booking.getId(), user.getId(), true));
    }

    @Test
    void getStatus_whenBookingStatusEqualsApproved_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.getStatus(booking.getId(), owner.getId(), true));
    }

    @Test
    void getStatus_whenApprovedEqualsFalse_thenReturnStatusRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Booking actualStatusBooking = bookingService.getStatus(booking.getId(), owner.getId(), false);
        assertEquals(BookingStatus.REJECTED, actualStatusBooking.getStatus(), "Метод отработал некорректно");
        assertEquals(item, actualStatusBooking.getItem(), "Метод отработал некорректно");
    }

    @Test
    void getBooking_whenInputValueValid_thenReturnBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking booking1 = bookingService.getBooking(booking.getId(), user.getId());

        assertEquals(booking1.getId(), booking.getId(), "Некорректно отработал метод");
        assertEquals(booking1.getBookingUser().getId(), user.getId(), "Некорректно отработал метод");
    }

    @Test
    void getBooking_whenUserIdNotFound_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(booking.getId(), user.getId()));
    }

    @Test
    void getBooking_whenBookingIdNotFound_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(booking.getId(), user.getId()));
    }

    @Test
    void getBooking_whenUserIdEqualsItemOwner_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(booking.getId(), 5));
    }


    @Test
    void getUserBookings_whenStatusEqualsAll_thenReturnBookingList() {
        Integer from = 1;
        Integer size = 10;
        LocalDateTime dateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingUserOrderByStartDesc(user, pageable)).thenReturn(list);
        List<Booking> orderBooking = bookingService.getUserBookings(user.getId(), "ALL", from, size);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "Некорректно отработал метод");
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(user, dateTime,
                dateTime.plusDays(1), pageable);
    }

    @Test
    void getUserBookings_whenStatusEqualsCURRENT_thenReturnBookingList() {
        Integer from = 1;
        Integer size = 10;
        LocalDateTime dateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> orderBooking = bookingService.getUserBookings(user.getId(), "CURRENT", from, size);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "Некорректно отработал метод");
        verify(bookingRepository, never()).findAllByBookingUserOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookings_whenStatusEqualsFUTURE_thenReturnBookingList() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingUserAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getUserBookings(user.getId(), "FUTURE", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "Некорректно отработал метод");
        verify(bookingRepository, never()).findAllByBookingUserOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookingUserAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookings_whenStatusEqualsPAST_thenReturnBookingList() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingUserAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getUserBookings(user.getId(), "PAST", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "Некорректно отработал метод");
        verify(bookingRepository, never()).findAllByBookingUserOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookings_whenStatusEqualsWAITING_thenReturnBookingList() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingUserAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getUserBookings(user.getId(), "WAITING", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "Некорректно отработал метод");
        verify(bookingRepository, never()).findAllByBookingUserOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookings_whenStatusEqualsREJECTED_thenReturnBookingList() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingUserAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getUserBookings(user.getId(), "REJECTED", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "Некорректно отработал метод");
        verify(bookingRepository, never()).findAllByBookingUserOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookingUserAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookingUserAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserBookings_whenStatusNotValid_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(user.getId(), "RRR", 1, 1));
    }

    @Test
    void getUserItems_whenStatusEqualsALL_thenReturnListBookings() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getUserItems(user.getId(), "ALL", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректно отработал метод");
        verify(bookingRepository, times(1))
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserItems_whenStatusEqualsCURRENT_thenReturnListBookings() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getUserItems(user.getId(), "CURRENT", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректно отработал метод");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserItems_whenStatusEqualsFUTURE_thenReturnListBookings() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getUserItems(user.getId(), "FUTURE", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректно отработал метод");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserItems_whenStatusEqualsPAST_thenReturnListBookings() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getUserItems(user.getId(), "PAST", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректно отработал метод");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserItems_whenStatusEqualsWAITING_thenReturnListBookings() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getUserItems(user.getId(), "WAITING", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректно отработал метод");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserItems_whenStatusEqualsREJECTED_thenReturnListBookings() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getUserItems(user.getId(), "REJECTED", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректно отработал метод");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getUserItems_whenStatusNotValid_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> bookingService.getUserItems(user.getId(),
                "AAA", 1, 1));
    }
}
