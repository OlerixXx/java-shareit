package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.pageable.ConvertPageable;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public Booking update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<Booking> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam String state,
                                        @RequestParam Integer from,
                                        @RequestParam Integer size) {
        return bookingService.getAllBookings(userId, state, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam String state,
                                            @RequestParam Integer from,
                                            @RequestParam Integer size) {
        return bookingService.getAllBookingItems(userId, state, ConvertPageable.toMakePage(from, size));
    }
}