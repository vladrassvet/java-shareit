package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookingUserOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findAllByBookingUserAndStartIsBeforeAndEndIsAfterOrderByStart(User user,
                                                                                LocalDateTime startDate, LocalDateTime endDate,
                                                                                Pageable pageable);

    List<Booking> findAllByBookingUserAndStartIsAfterOrderByStartDesc(User user, LocalDateTime startDate, Pageable pageable);

    List<Booking> findAllByBookingUserAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime startDate, Pageable pageable);

    List<Booking> findAllByBookingUserAndStatusEqualsOrderByStartDesc(User user, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(User user,
                                                                              LocalDateTime startDate, LocalDateTime endDate,
                                                                              Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime startDate, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime startDate, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEqualsOrderByStartDesc(User user, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(long itemId,
                                                                           LocalDateTime startDate, LocalDateTime endDate);

    Booking findFirstByItemIdAndStartIsAfterOrderByStart(long itemId, LocalDateTime startDate);

    Booking findFirstByBookingUserIdAndItemIdAndEndIsBeforeOrderByEndDesc(long userId, long itemId, LocalDateTime startDate);

}
