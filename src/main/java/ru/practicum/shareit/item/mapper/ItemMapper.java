package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user
        );
    }

    public static ItemRequestDto toItemRequest(Item item, Booking nextBooking, Booking lastBooking, List<CommentRequestDto> comments) {
        return new ItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                lastBooking == null ? null : BookingMapper.toRequest(lastBooking),
                nextBooking == lastBooking || nextBooking == null ? null : BookingMapper.toRequest(nextBooking),
                comments == null ? new ArrayList<>() : comments
        );
    }

    public static Item updateItem(Item oldItem, ItemDto newItem) {
        if (StringUtils.hasText(newItem.getName())) {
            oldItem.setName(newItem.getName());
        }
        if (StringUtils.hasText(newItem.getDescription())) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }
}
