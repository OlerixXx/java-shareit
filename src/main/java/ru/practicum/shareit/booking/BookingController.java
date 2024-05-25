package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.groups.Create;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Create.class) BookingDto bookingDto) {
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
    public List<Booking> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "ALL") String state){
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "ALL") String state){
        return bookingService.getAllBookingItems(userId, state);
    }
}
