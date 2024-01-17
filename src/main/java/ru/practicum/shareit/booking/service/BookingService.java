package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(long userId, BookingDtoRequest bookingDtoRequest);

    Booking getStatus(long bookingId, long userId, Boolean approved);

    Booking getBooking(long bookingId, long userId);

    List<Booking> getUserBookings(long userId, String status);

    List<Booking> getUserItems(long userId, String status);
}
