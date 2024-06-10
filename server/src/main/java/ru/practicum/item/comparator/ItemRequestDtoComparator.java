package ru.practicum.item.comparator;

import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.item.dto.ItemRequestDto;

import java.util.Comparator;

public class ItemRequestDtoComparator implements Comparator<ItemRequestDto> {

    @Override
    public int compare(ItemRequestDto o1, ItemRequestDto o2) {
        int idComparison = o1.getId().compareTo(o2.getId());
        if (idComparison != 0) {
            return idComparison;
        }

        int lastBookingComparison = compareNullableBookings(o1.getLastBooking(), o2.getLastBooking());
        if (lastBookingComparison != 0) {
            return lastBookingComparison;
        }

        return compareNullableBookings(o1.getNextBooking(), o2.getNextBooking());
    }

    private int compareNullableBookings(BookingRequestDto b1, BookingRequestDto b2) {
        if (b1 == null && b2 == null) {
            return -1;
        } else {
            return 1;
        }
    }
}