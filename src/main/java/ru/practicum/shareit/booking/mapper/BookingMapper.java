package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDtoResp toResponse(Booking booking) {
        ItemDto itemDto = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        UserDto owner = UserDto.builder()
                .id(booking.getBookingUser().getId())
                .build();

        return BookingDtoResp.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(owner)
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User user, BookingStatus bookingStatus) {

        return Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .bookingUser(user)
                .status(bookingStatus)
                .build();
    }

    public static BookingShort toBookingShort(Booking booking) {

        return BookingShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBookingUser().getId())
                .build();
    }
}
