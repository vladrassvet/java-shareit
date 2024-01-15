package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking addBooking(long userId, BookingDtoRequest bookingDtoRequest) {
        checkUser(userId);
        LocalDateTime start = bookingDtoRequest.getStart();
        LocalDateTime end = bookingDtoRequest.getEnd();
        if (start.isAfter(end) || start.equals(end)) {
            log.info("Некорректно заданы параметры бронирования");
            throw new ValidationException("Некорректно заданы параметры бронирования");
        }
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с  id " + bookingDtoRequest.getItemId()));
        if (user.getId() == item.getOwner().getId()) {
            log.info("Пользователь пытается забронировать свою вещь");
            throw new NotFoundException("Пользователь не может забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            log.info("Пытаются забронировать недоступную вещь");
            throw new ValidationException("Невозможно забронировать данную вещь");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDtoRequest, item, user, BookingStatus.WAITING));
        return bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с  id " + booking.getId()));
    }

    @Override
    public Booking getStatus(long bookingId, long userId, Boolean approved) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с  id " + bookingId));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            log.info("операцию по подтверждению или отклонению бронирования пытается выполнить не владелец вещи");
            throw new NotFoundException("Только собственник вещи может выполнить бронирование или отменить его");
        }
        if (approved) {
            if (booking.getStatus() == BookingStatus.APPROVED) {
                log.info("Заявка на бронирование уже подтверждена");
                throw new ValidationException("Заявка на бронирование уже подтверждена");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("добавлено новое бронирование = {}", booking);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с  id " + bookingId));
    }

    @Override
    public Booking getBooking(long bookingId, long userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с  id " + bookingId));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId() && userId != booking.getBookingUser().getId()) {
            log.info("не достаточно прав для запроса");
            throw new NotFoundException("У Вас не достаточно прав для запроса данной информации");
        }
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(long userId, String status) {
        checkUser(userId);
        User user = userService.getUserById(userId);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList;
        switch (status) {
            case "ALL":
                bookingList = bookingRepository.findAllByBookingUserOrderByStartDesc(user);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(user,
                        dateTime, dateTime);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByBookingUserAndStartIsAfterOrderByStartDesc(user, dateTime);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByBookingUserAndEndIsBeforeOrderByStartDesc(user, dateTime);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByBookingUserAndStatusEqualsOrderByStartDesc(user,
                        BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByBookingUserAndStatusEqualsOrderByStartDesc(user,
                        BookingStatus.REJECTED);
                break;
            default:
                log.info("Передан неверный статус");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookingList;
    }

    @Override
    public List<Booking> getUserItems(long userId, String status) {
        checkUser(userId);
        User user = userService.getUserById(userId);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList;
        switch (status) {
            case "ALL":
                bookingList = bookingRepository.findAllByItemOwnerOrderByStartDesc(user);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(user,
                        dateTime, dateTime);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(user, dateTime);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(user, dateTime);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(user,
                        BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(user,
                        BookingStatus.REJECTED);
                break;
            default:
                log.info("Передан неверный статус");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList;
    }

    private void checkUser(long checkId) {
        if (!userRepository.existsById(checkId)) {
            log.info("Пользователя с таким id = " + checkId + " не существует");
            throw new NotFoundException("Пользователя с таким id = " + checkId + " не существует");
        }
    }
}
