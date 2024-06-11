package ru.practicum.booking.mapper;

import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                Status.WAITING
        );
    }

    public static BookingRequestDto toRequest(Booking booking) {
        return new BookingRequestDto(
                booking.getId() == null ? null : booking.getId(),
                booking.getBooker().getId() == null ? null : booking.getBooker().getId()
        );
    }
}