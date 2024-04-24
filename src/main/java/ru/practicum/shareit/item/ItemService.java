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
        item.setOwner(userRepository.getUser(userId).getName());
        return itemRepository.create(item);
    }

    public Item update(Long userId, Item item) {
        if (!ownerMatches(userId, item.getId())) {
            throw new NoSuchElementException();
        } else {
            return itemRepository.update(item);
        }
    }

    public Item getItem(Long userId, Long itemId) {
        if (!userRepository.isExist(userId)) {
            throw new NoSuchElementException();
        }
        return itemRepository.getItem(itemId);
    }

    public List<Item> getAll(Long userId) {
        User user = userRepository.getUser(userId);
        return itemRepository.getAll().stream()
                .filter(item -> item.getOwner().equals(user.getName()))
                .collect(Collectors.toList());
    }

    public List<Item> search(Long userId, String text) {
        if (!userRepository.isExist(userId)) {
            throw new NoSuchElementException();
        }
        return itemRepository.search(text);
    }

    private boolean ownerMatches(Long userId, Long itemId) {
        return userRepository.getUser(userId).getName().equals(itemRepository.getItem(itemId).getOwner());
    }

}
