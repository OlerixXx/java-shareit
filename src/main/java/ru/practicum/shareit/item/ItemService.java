package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long userId, Item item);

    Item update(Long userId, Item item);

    Item getItem(Long userId, Long itemId);

    List<Item> getAll(Long userId);

    List<Item> search(Long userId, String text);
}
