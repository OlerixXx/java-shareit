package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.model.Item;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking create(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        Item item = itemRepository.findByIdAndOwnerIdIsNot(bookingDto.getItemId(), userId).orElseThrow(NoSuchElementException::new);
        if (item.getAvailable()) {
            return bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public Booking update(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId).orElseThrow(NoSuchElementException::new);
        if (booking.getStatus() == Status.APPROVED) {
            throw new IllegalArgumentException();
        } else {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
            return bookingRepository.save(booking);
        }

    }

    public Booking getBooking(Long userId, Long bookingId) {
        return bookingRepository.findByIdAndBookerIdOrItemOwnerId(bookingId, userId, userId).orElseThrow(NoSuchElementException::new);
    }

    public List<Booking> getAllBookings(Long userId, String state, Pageable page) {
        if (!userRepository.userExists(userId)) {
            throw new NoSuchElementException();
        }
        List<Status> statusesInQuery = Status.toStatus(state);
        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(
                        userId, statusesInQuery, LocalDateTime.now(), LocalDateTime.now(),
                        PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("start").ascending())
                );
            case "PAST":
                return bookingRepository.findAllByBookerIdAndStatusInAndEndBefore(
                        userId, statusesInQuery, LocalDateTime.now(),
                        PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("end").descending())
                );
            default:
                return bookingRepository.findAllByBookerIdAndStatusIn(
                        userId, statusesInQuery,
                        PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("start").descending())
                );
        }
    }

    public List<Booking> getAllBookingItems(Long userId, String state, Pageable page) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        List<Status> statusesInQuery = Status.toStatus(state);
        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllByItemInAndStatusInAndStartBeforeAndEndAfter(
                        itemRepository.findAllByOwner(user), statusesInQuery, LocalDateTime.now(), LocalDateTime.now(),
                        PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("start").ascending())
                );
            case "PAST":
                return bookingRepository.findAllByItemInAndStatusInAndEndBefore(
                        itemRepository.findAllByOwner(user), statusesInQuery, LocalDateTime.now(),
                        PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("end").descending())
                );
            default:
                return bookingRepository.findAllByItemInAndStatusIn(
                        itemRepository.findAllByOwner(user), statusesInQuery,
                        PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("start").descending())
                );
        }
    }

}
