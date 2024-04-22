package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item);

    Item update(Item item);

    Item getItem(Long itemId);

    List<Item> getAll();

    List<Item> search(String text);

}
