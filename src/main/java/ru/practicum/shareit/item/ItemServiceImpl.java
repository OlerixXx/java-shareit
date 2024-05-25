package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.comparator.ItemRequestDtoComparator;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Item create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(userId).orElseThrow(NoSuchElementException::new));
        return itemRepository.save(item);
    }

    @Transactional
    public CommentRequestDto create(Long userId, Long itemId, CommentDto commentDto) {
        if (bookingRepository.existsBookingsByItemAndBookerAndStatus(
                itemRepository.findById(itemId).orElseThrow(),
                userRepository.findById(userId).orElseThrow(),
                Status.APPROVED) &&
                bookingRepository.existBookingBeforeNow(LocalDateTime.now(), itemId, userId).isPresent()
        ) {
            Comment comment = CommentMapper.toComment(
                    commentDto,
                    itemRepository.findById(itemId).orElseThrow(),
                    userRepository.findById(userId).orElseThrow()
            );
            commentRepository.save(comment);
            return CommentMapper.toCommentRequestDto(comment);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        if (!ownerMatches(userId, itemId)) {
            throw new NoSuchElementException();
        }
        return itemRepository.save(ItemMapper.updateItem(
                itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new),
                itemDto)
        );
    }

    public ItemRequestDto getItem(Long userId, Long itemId) {
        return getItemRequestDto(userId, itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new));
    }

    public List<ItemRequestDto> getAll(Long userId) {
        List<Item> itemList = itemRepository.findAllByOwner(userRepository.findById(userId).orElseThrow(NoSuchElementException::new));
        return itemList.stream()
                .map(item -> getItemRequestDto(userId, item))
                .sorted(new ItemRequestDtoComparator())
                .collect(Collectors.toList());
    }

    public List<Item> search(Long userId, String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
    }

    private boolean ownerMatches(Long userId, Long itemId) {
        return Objects.equals(itemRepository.findById(itemId).orElseThrow().getOwner().getId(), userId);
    }

    private ItemRequestDto getItemRequestDto(Long userId, Item item) {
        Booking nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), item.getId()).orElse(null);
        Booking lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), item.getId()).orElse(null);
        List<CommentRequestDto> comments = CommentMapper.toListCommentRequestDto(commentRepository.findAllByItem(item));
        if (item.getOwner().getId() == userId && lastBooking != null) {
            return ItemMapper.toItemRequest(item, nextBooking, lastBooking, comments);
        } else {
            return ItemMapper.toItemRequest(item, null, null, comments);
        }
    }
}