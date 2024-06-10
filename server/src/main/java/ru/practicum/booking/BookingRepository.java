package ru.practicum.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long userId);

    @Query(value = "select b from Booking as b where b.id = ?1 AND (b.booker.id = ?2 OR b.item.owner.id = ?3)")
    Optional<Booking> findByIdAndBookerIdOrItemOwnerId(Long bookingId, Long userId1, Long userId2);

    List<Booking> findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(Long booker, List<Status> statusList, LocalDateTime firstNow, LocalDateTime secondNow, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusInAndEndBefore(Long booker, List<Status> statusList, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusInAndStartAfterOrderByStartAsc(Long booker, List<Status> statusList, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusInAndStartBeforeAndEndAfter(List<Item> itemList, List<Status> statusList, LocalDateTime firstNow, LocalDateTime secondNow, Pageable pageable);

    List<Booking> findAllByItemInAndStatusInAndEndBefore(List<Item> itemList, List<Status> statusList, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemInAndStatusInAndStartAfterOrderByStartAsc(List<Item> itemList, List<Status> statusList, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusIn(Long booker, List<Status> statusList, Pageable pageable);

    List<Booking> findAllByItemInAndStatusIn(List<Item> itemList, List<Status> statusList, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE end_date >= ?1 AND item_id = ?2 ORDER BY end_date LIMIT 1", nativeQuery = true)
    Optional<Booking> findNextBooking(LocalDateTime now, Long itemId);

    @Query(value = "SELECT * FROM bookings WHERE start_date < ?1 AND item_id = ?2 AND status = 'APPROVED' ORDER BY start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> findLastBooking(LocalDateTime now, Long itemId);

    @Query(value = "SELECT EXISTS (SELECT * FROM bookings WHERE item_id = ?1 AND booker_id = ?2 AND status = ?3) OR EXISTS (SELECT * FROM bookings WHERE end_date <= ?4 AND item_id = ?1 AND booker_id = ?2 ORDER BY end_date LIMIT 1)", nativeQuery = true)
    Boolean existsBookings(Long itemId, Long bookerId, String status, LocalDateTime now);
}