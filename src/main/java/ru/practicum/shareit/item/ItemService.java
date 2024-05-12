package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Item create(Long userId, Item item) {
        item.setOwner(userRepository.findById(userId).orElseThrow().getName());
        return itemRepository.save(item);
    }

    public Item update(Long userId, Item item) {
        if (!ownerMatches(userId, item.getId())) {
            throw new NoSuchElementException();
        } else {
            return itemRepository.save(item);
        }
    }

    public Item getItem(Long userId, Long itemId) {
        if (!userRepository.isExists(userId)) {
            throw new NoSuchElementException();
        }
        return itemRepository.findById(itemId).orElseThrow();
    }

    public List<Item> getAll(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().equals(user.getName()))
                .collect(Collectors.toList());
    }

    public List<Item> search(Long userId, String text) {
        if (!userRepository.isExists(userId)) {
            throw new NoSuchElementException();
        }
        return itemRepository.findByNameContaining(text);
    }

    private boolean ownerMatches(Long userId, Long itemId) {
        return userRepository.findById(userId).orElseThrow().getName().equals(itemRepository.findById(itemId).orElseThrow().getOwner());
    }

}
