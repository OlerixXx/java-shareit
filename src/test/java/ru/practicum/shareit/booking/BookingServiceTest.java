package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pageable.ConvertPageable;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;

    BookingService bookingService;

    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository);

        user = new User(
                1L,
                "user",
                "email@mail.ru",
                LocalDateTime.now()
        );

        item = new Item(
                1L,
                "item",
                "description",
                true,
                user,
                null
        );

        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        booking = new Booking(
                1L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                Status.WAITING
        );

    }

    @Test
    void create_whenBookingAvailableIsTrue_thenSavedBooking() {
        Booking bookingToSave = booking;
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerIdIsNot(1L, user.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item))).thenReturn(bookingToSave);

        Booking actualBooking = bookingService.create(user.getId(), bookingDto);

        assertEquals(actualBooking, bookingToSave);
    }

    @Test
    void create_whenBookingAvailableIsFalse_thenIllegalArgumentExceptionThrow() {
        Item itemSave = item;
        itemSave.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerIdIsNot(1L, user.getId())).thenReturn(Optional.of(item));
        assertThrows(IllegalArgumentException.class, () -> bookingService.create(user.getId(), bookingDto));
    }

    @Test
    void update_whenStatusIsWaitingAndPassedTrue_thenReturnUpdatedBookingStatusApproved() {
        when(bookingRepository.findByIdAndItemOwnerId(1L, 1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking actualBooking = bookingService.update(user.getId(), 1L, true);

        assertEquals(actualBooking.getStatus(), Status.APPROVED);
    }

    @Test
    void update_whenStatusIsWaitingAndPassedFalse_thenReturnUpdatedBookingStatusRejected() {
        when(bookingRepository.findByIdAndItemOwnerId(1L, 1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking actualBooking = bookingService.update(user.getId(), 1L, false);

        assertEquals(actualBooking.getStatus(), Status.REJECTED);
    }

    @Test
    void update_whenStatusIsAlreadyApproved_thenIllegalArgumentExceptionThrow() {
        Booking actualBooking = booking;
        actualBooking.setStatus(Status.APPROVED);
        when(bookingRepository.findByIdAndItemOwnerId(1L, 1L)).thenReturn(Optional.of(booking));
        assertThrows(IllegalArgumentException.class, () -> bookingService.update(user.getId(), 1L, true));
    }

    @Test
    void getBooking_whenBookingExists_thenReturnBooking() {
        when(bookingRepository.findByIdAndBookerIdOrItemOwnerId(booking.getId(), user.getId(), user.getId())).thenReturn(Optional.of(booking));
        Booking actualBooking = bookingService.getBooking(user.getId(), booking.getId());
        assertEquals(booking, actualBooking);
    }

    @Test
    void getAllBookings_whenUserNotExists_thenNoSuchElementExceptionThrow() {
        when(userRepository.userExists(user.getId())).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookings(user.getId(), "ALL", null));
    }

    @Test
    void getAllBookings_whenStateAll_thenReturnAllBookings() {
        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatusIn(any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> list = bookingService.getAllBookings(user.getId(), "ALL", ConvertPageable.toMakePage(0, 10));

        assertAll(
                () -> assertEquals(list.size(), 1),
                () ->assertEquals(list.get(0), booking)
        );
    }

    @Test
    void getAllBookings_whenStateCurrent_thenReturnOnlyCurrentBookings() {
        Booking booking1 = booking;
        booking1.setStatus(Status.APPROVED);
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(
                eq(user.getId()), eq(Status.toStatus("CURRENT")), any(LocalDateTime.class), any(LocalDateTime.class), any()
        )).thenReturn(List.of(booking1));

        List<Booking> list = bookingService.getAllBookings(user.getId(), "CURRENT", ConvertPageable.toMakePage(0, 10));

        assertAll(
                () -> assertEquals(list.size(), 1),
                () -> assertEquals(list.get(0), booking1)
        );
    }

    @Test
    void getAllBookings_whenStatePast_thenReturnOnlyPastBookings() {
        Booking booking1 = booking;
        booking1.setStatus(Status.APPROVED);
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));

        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatusInAndEndBefore(
                eq(user.getId()), eq(Status.toStatus("PAST")), any(LocalDateTime.class), any()
        )).thenReturn(List.of(booking1));

        List<Booking> list = bookingService.getAllBookings(user.getId(), "PAST", ConvertPageable.toMakePage(0, 10));

        assertAll(
                () -> assertEquals(list.size(), 1),
                () -> assertEquals(list.get(0), booking1)
        );
    }

    @Test
    void getAllBookingItems_whenStateAll_thenReturnAllBookingsByItems() {
        Booking booking1 = booking;
        booking1.setStatus(Status.WAITING);
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwner(user)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemInAndStatusIn(any(), any(), any())).thenReturn(List.of(booking1));

        List<Booking> list = bookingService.getAllBookingItems(user.getId(), "ALL", ConvertPageable.toMakePage(0, 10));

        assertAll(
                () -> assertEquals(list.size(), 1),
                () -> assertEquals(list.get(0), booking1)
        );
    }

    @Test
    void getAllBookingItems_whenStateCurrent_thenReturnOnlyCurrentBookingsByItems() {
        Booking booking1 = booking;
        booking1.setStatus(Status.APPROVED);
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwner(user)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemInAndStatusInAndStartBeforeAndEndAfter(
                eq(List.of(item)), eq(Status.toStatus("CURRENT")), any(LocalDateTime.class), any(LocalDateTime.class), any()
        )).thenReturn(List.of(booking1));

        List<Booking> list = bookingService.getAllBookingItems(user.getId(), "CURRENT", ConvertPageable.toMakePage(0, 10));

        assertAll(
                () -> assertEquals(list.size(), 1),
                () -> assertEquals(list.get(0), booking1)
        );
    }

    @Test
    void getAllBookingItems_whenStatePast_thenReturnOnlyPastBookingsByItems() {
        Booking booking1 = booking;
        booking1.setStatus(Status.APPROVED);
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwner(user)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemInAndStatusInAndEndBefore(
                eq(List.of(item)), eq(Status.toStatus("PAST")), any(LocalDateTime.class), any()
        )).thenReturn(List.of(booking1));

        List<Booking> list = bookingService.getAllBookingItems(user.getId(), "PAST", ConvertPageable.toMakePage(0, 10));

        assertAll(
                () -> assertEquals(list.size(), 1),
                () -> assertEquals(list.get(0), booking1)
        );
    }

    @Test
    void toStatus_whileStatusIsNotCorrect_thenReturnIllegalArgumentExceptionThrow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Status.toStatus("UNKNOWN");
        });

        assertEquals("Unknown state: " + "UNKNOWN", exception.getMessage());
    }

    @Test
    void toRequest_whileStatusIsNotCorrect_thenReturnIllegalArgumentExceptionThrow() {
        User userRequest = user;
        Booking bookingRequest = booking;
        userRequest.setId(null);
        bookingRequest.setId(null);
        bookingRequest.setBooker(user);
        BookingRequestDto bookingRequestDto = BookingMapper.toRequest(bookingRequest);

        assertAll(
                () -> assertNull(bookingRequestDto.getId()),
                () -> assertNull(bookingRequestDto.getBookerId())
        );
    }
}
