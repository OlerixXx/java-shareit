package ru.practicum.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.item.dto.*;
import ru.practicum.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemUserRequestIdDto create(Long userId, ItemDto itemDto);

    CommentRequestDto create(Long userId, Long itemId, CommentDto commentDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    ItemRequestDto getItem(Long userId, Long itemId);

    List<ItemRequestDto> getAll(Long userId, Pageable page);

    List<Item> search(Long userId, String text, Pageable page);
}
