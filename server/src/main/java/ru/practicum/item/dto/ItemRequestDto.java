package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingRequestDto lastBooking;
    private BookingRequestDto nextBooking;
    private List<CommentRequestDto> comments;
}