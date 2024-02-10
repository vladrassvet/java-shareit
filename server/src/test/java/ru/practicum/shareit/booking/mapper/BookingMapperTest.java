package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
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
    void toResponse() {
        BookingDtoResp bookingDtoResp = BookingMapper.toResponse(booking);
        assertEquals(item.getName(), bookingDtoResp.getItem().getName());
        assertEquals(booking.getId(), bookingDtoResp.getId());
        assertEquals(booking.getStart(), bookingDtoResp.getStart());
    }

    @Test
    void toBooking() {
        Booking booking1 = BookingMapper.toBooking(request, item, user, BookingStatus.WAITING);
        assertEquals(BookingStatus.WAITING, booking1.getStatus());
        assertEquals(user, booking1.getBookingUser());
        assertEquals(item, booking1.getItem());
    }

    @Test
    void toBookingShort() {
        BookingShort bookingShort = BookingMapper.toBookingShort(booking);
        assertEquals(booking.getId(), bookingShort.getId());
        assertEquals(booking.getBookingUser().getId(), bookingShort.getBookerId());
    }
}