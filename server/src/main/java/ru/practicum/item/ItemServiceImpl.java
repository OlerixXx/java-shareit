package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.item.comparator.ItemRequestDtoComparator;
import ru.practicum.item.dto.*;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public ItemUserRequestIdDto create(Long userId, ItemDto itemDto) {
        Request request;
        if (itemDto.getRequestId() == null) {
            request = null;
        } else {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(NoSuchElementException::new);
        }
        Item item = ItemMapper.toItem(itemDto,
                userRepository.findById(userId).orElseThrow(NoSuchElementException::new),
                request
        );
        itemRepository.save(item);
        return ItemMapper.toItemUserRequestIdDto(item);
    }

    @Transactional
    public CommentRequestDto create(Long userId, Long itemId, CommentDto commentDto) {
        if (bookingRepository.existsBookings(itemId, userId, Status.APPROVED, LocalDateTime.now())) {
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

    public List<ItemRequestDto> getAll(Long userId, Pageable page) {
        List<Item> itemList = itemRepository.findAllByOwner(userRepository.findById(userId).orElseThrow(NoSuchElementException::new), page);
        return itemList.stream()
                .map(item -> getItemRequestDto(userId, item))
                .sorted(new ItemRequestDtoComparator())
                .collect(Collectors.toList());
    }

    public List<Item> search(Long userId, String text, Pageable page) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text, page);
    }

    private boolean ownerMatches(Long userId, Long itemId) {
        return Objects.equals(itemRepository.findById(itemId).orElseThrow().getOwner().getId(), userId);
    }

    private ItemRequestDto getItemRequestDto(Long userId, Item item) {
        Booking nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), item.getId()).orElse(null);
        Booking lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), item.getId()).orElse(null);
        List<CommentRequestDto> comments = CommentMapper.toListCommentRequestDto(commentRepository.findAllByItem(item));
        if (item.getOwner().getId().equals(userId) && lastBooking != null) {
            return ItemMapper.toItemRequest(item, nextBooking, lastBooking, comments);
        } else {
            return ItemMapper.toItemRequest(item, null, null, comments);
        }
    }
}