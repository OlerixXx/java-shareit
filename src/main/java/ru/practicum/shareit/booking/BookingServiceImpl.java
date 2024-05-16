package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking create(Long userId, BookingDto bookingDto) {
        return bookingRepository.save(BookingMapper.toBooking(
                bookingDto,
                userRepository.findById(userId).orElseThrow(),
                itemRepository.findById(bookingDto.getItem_id()).orElseThrow())
        );
    }

    @Transactional
    public Booking update(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long userId, Long bookingId) {
        return null;
    }

    public List<Booking> getAllBookings(Long userId, Status state) {
        return List.of();
    }

    public List<Booking> getAllBookingItems(Long userId, Status state) {
        return List.of();
    }

}
