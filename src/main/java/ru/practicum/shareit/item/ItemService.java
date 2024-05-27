package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long userId, ItemDto itemDto);

    CommentRequestDto create(Long userId, Long itemId, CommentDto commentDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    ItemRequestDto getItem(Long userId, Long itemId);

    List<ItemRequestDto> getAll(Long userId);

    List<Item> search(Long userId, String text);
}
