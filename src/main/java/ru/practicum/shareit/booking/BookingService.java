package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingService {
    Booking create(Long userId, BookingDto bookingDto);

    Booking update(Long userId, Long bookingId, boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getAllBookings(Long userId, Status state);

    List<Booking> getAllBookingItems(Long userId, Status state);
}
