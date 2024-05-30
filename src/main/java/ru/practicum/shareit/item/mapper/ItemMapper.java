package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );
    }

    public static Item toItem(ItemDto itemDto, User user, Request request) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                request
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
                nextBooking == null || nextBooking.equals(lastBooking) ? null : BookingMapper.toRequest(nextBooking),
                comments == null ? new ArrayList<>() : comments
        );
    }

    public static ItemUserRequestIdDto toItemUserRequestIdDto(Item item) {
        return new ItemUserRequestIdDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() == null ? null : item.getRequest().getId()
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

    public static List<ItemRequestWithoutUserDto> toListItemRequestWithoutUserDto(List<Item> items) {
        return items.stream()
                .map(item -> new ItemRequestWithoutUserDto (
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        item.getRequest() == null ? null : item.getRequest().getId()))
                .collect(Collectors.toList());
    }
}
