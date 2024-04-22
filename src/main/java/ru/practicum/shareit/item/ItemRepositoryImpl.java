package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new TreeMap<>();
    private Long lastId = 0L;

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.computeIfPresent(item.getId(), (itemId, itemInMap) -> {
            item.setName(Optional.ofNullable(item.getName()).orElse(itemInMap.getName()));
            item.setDescription(Optional.ofNullable(item.getDescription()).orElse(itemInMap.getDescription()));
            item.setAvailable(Optional.ofNullable(item.getAvailable()).orElse(itemInMap.getAvailable()));
            item.setOwner(Optional.ofNullable(item.getOwner()).orElse(itemInMap.getOwner()));
            return item;
        });
        return item;
    }

    @Override
    public Item getItem(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> search(String text) {
        if (!text.isEmpty()) {
            return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()) & item.getAvailable())
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private Long generateId() {
        lastId++;
        return lastId;
    }
}
