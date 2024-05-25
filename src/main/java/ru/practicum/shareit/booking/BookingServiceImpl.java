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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(NoSuchElementException::new);
        User user = userExists(userId);
        if (item.getOwner().getId() == userId) {
            throw new NoSuchElementException();
        } else if (item.getAvailable()) {
            return bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        } else { throw new IllegalArgumentException(); }
    }

    @Transactional
    public Booking update(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NoSuchElementException::new);
        if (booking.getStatus() == Status.APPROVED) {
            throw new IllegalArgumentException();
        } else if (booking.getItem().getOwner().getId() == userId) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
            return bookingRepository.save(booking);
        } else {
            throw new NoSuchElementException();
        }

    }

    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NoSuchElementException::new);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return booking;
        } else {
            throw new NoSuchElementException();
        }
    }

    public List<Booking> getAllBookings(Long userId, String state) {
        userExists(userId);
        List<Status> statusesInQuery = Status.toStatus(state);
        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId, statusesInQuery, LocalDateTime.now(), LocalDateTime.now()
                );
            case "PAST":
                return bookingRepository.findAllByBookerIdAndStatusInAndEndBeforeOrderByEndDesc(
                        userId, statusesInQuery, LocalDateTime.now()
                );
            default:
                return bookingRepository.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statusesInQuery);
        }
    }

    public List<Booking> getAllBookingItems(Long userId, String state) {
        User user = userExists(userId);
        List<Status> statusesInQuery = Status.toStatus(state);
        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllByItemInAndStatusInAndStartBeforeAndEndAfterOrderByStartAsc(
                        itemRepository.findAllByOwner(user), statusesInQuery, LocalDateTime.now(), LocalDateTime.now()
                );
            case "PAST":
                return bookingRepository.findAllByItemInAndStatusInAndEndBeforeOrderByEndDesc(
                        itemRepository.findAllByOwner(user), statusesInQuery, LocalDateTime.now()
                );
            default:
                return bookingRepository.findAllByItemInAndStatusInOrderByStartDesc(
                        itemRepository.findAllByOwner(user), statusesInQuery
                );
        }
    }

    private User userExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
    }

}
