package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.pageable.ConvertPageable;

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
    public List<Booking> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookings(userId, state, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "ALL") String state,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookingItems(userId, state, ConvertPageable.toMakePage(from, size));
    }
}
