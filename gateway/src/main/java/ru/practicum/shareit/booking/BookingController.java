package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) long userId,
                                             @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("Создано бронирование по пользователю с id {}", userId);
        return bookingClient.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmationOrRejectionOfBooking(
            @PathVariable long bookingId, @RequestHeader(USER_ID) long userId, @RequestParam(name = "approved")
    Boolean approved) {
        if (approved) {
            log.info("Бронирование с bookingId {} , принято.", bookingId);
        } else {
            log.info("Бронирование с bookingId {} , отклонено.", bookingId);
        }
        return bookingClient.getStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId, @RequestHeader(USER_ID) long userId) {
        log.info("бронирование с bookingId {}, пользователя с userId  {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsUser(@RequestHeader(USER_ID) long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @Valid @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                     @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        log.info("все бронирования пользователя с userId {} получены", userId);
        return bookingClient.getUserBookings(userId, state, from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader(USER_ID) long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @Valid @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                  @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        log.info("получены все бронирования собственника с userId {}, на его вещи", userId);
        return bookingClient.getUserBookings(userId, state, from, size, true);
    }
}
