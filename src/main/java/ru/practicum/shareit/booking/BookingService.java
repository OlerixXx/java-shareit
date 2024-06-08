package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking create(Long userId, BookingDto bookingDto);

    Booking update(Long userId, Long bookingId, boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getAllBookings(Long userId, String state, Pageable page);

    List<Booking> getAllBookingItems(Long userId, String state, Pageable page);
}
