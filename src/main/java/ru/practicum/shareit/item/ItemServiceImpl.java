package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Item create(Long userId, Item item) {
        item.setOwner(userRepository.findById(userId).orElseThrow(NoSuchElementException::new));
        return itemRepository.save(item);
    }

    @Transactional
    public Item update(Long userId, Item item) {
        if (!ownerMatches(userId, item.getId())) {
            throw new NoSuchElementException();
        } else {
            Item oldItem = itemRepository.findById(item.getId()).orElseThrow(NoSuchElementException::new);

            if (!StringUtils.hasText(item.getName())) {
                item.setName(oldItem.getName());
            }
            if (!StringUtils.hasText(item.getDescription())) {
                item.setDescription(oldItem.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(oldItem.getAvailable());
            }
            item.setOwner(oldItem.getOwner());
            return itemRepository.save(item);
        }
    }

    public Item getItem(Long userId, Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
    }

    public List<Item> getAll(Long userId) {
        return itemRepository.findAllByOwner(userRepository.findById(userId).orElseThrow(NoSuchElementException::new));
        /*      User user = userRepository.findById(userId).orElseThrow();
                return itemRepository.findAll().stream()
                    .filter(item -> item.getOwner().equals(user.getName()))
                    .collect(Collectors.toList());*/
    }

    public List<Item> search(Long userId, String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
    }

    private boolean ownerMatches(Long userId, Long itemId) {
        // return userRepository.findById(userId).orElseThrow().getName().equals(itemRepository.findById(itemId).orElseThrow().getOwner());
        return Objects.equals(itemRepository.findById(itemId).orElseThrow().getOwner().getId(), userId);
    }
}