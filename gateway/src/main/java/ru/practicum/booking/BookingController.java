package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.groups.Create;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Create.class) BookingDto bookingDto) {
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "ALL") String state,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingClient.getAllBookingItems(userId, state, from, size);
    }
}