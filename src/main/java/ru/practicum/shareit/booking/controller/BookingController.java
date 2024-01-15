package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResp addBooking(@RequestHeader(USER_ID) long userId,
                                     @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        Booking booking = bookingService.addBooking(userId, bookingDtoRequest);
        return BookingMapper.toResponse(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResp confirmationOrRejectionOfBooking(
            @PathVariable long bookingId, @RequestHeader(USER_ID) long userId, @RequestParam(name = "approved")
    Boolean approved) {
        Booking booking = bookingService.getStatus(bookingId, userId, approved);
        return BookingMapper.toResponse(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResp getBooking(@PathVariable long bookingId, @RequestHeader(USER_ID) long userId) {
        Booking booking = bookingService.getBooking(bookingId, userId);
        return BookingMapper.toResponse(booking);
    }

    @GetMapping
    public List<BookingDtoResp> getAllBookingsUser(@RequestHeader(USER_ID) long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String state) {
        List<Booking> bookings = bookingService.getUserBookings(userId, state);
        return bookings.stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResp> getAllItemsUser(@RequestHeader(USER_ID) long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String state) {
        List<Booking> bookings = bookingService.getUserItems(userId, state);
        return bookings.stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }
}
