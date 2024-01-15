package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookingUserOrderByStartDesc(User user);

    List<Booking> findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(User user,
                                                                                LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByBookingUserAndStartIsAfterOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findAllByBookingUserAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findAllByBookingUserAndStatusEqualsOrderByStartDesc(User user, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User user);

    List<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(User user,
                                                                              LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByItemOwnerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findAllByItemOwnerAndStatusEqualsOrderByStartDesc(User user, BookingStatus status);

    Booking findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(long itemId,
                                                                           LocalDateTime now1, LocalDateTime now2);

    Booking findFirstByItemIdAndStartIsAfterOrderByStart(long itemId, LocalDateTime now);

    Booking findFirstByBookingUserIdAndItemIdAndEndIsBeforeOrderByEndDesc(long userId, long itemId, LocalDateTime now);

}
