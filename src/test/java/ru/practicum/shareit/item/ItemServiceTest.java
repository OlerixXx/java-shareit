package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.comparator.ItemRequestDtoComparator;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;

    ItemService itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Request request;
    private Comment comment;
    private CommentDto commentDto;
    private Booking lastBooking;
    private Booking nextBooking;


    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository, requestRepository);

        user = new User(
                1L,
                "user",
                "email@mail.ru",
                LocalDateTime.now()
        );

        item = new Item(
                1L,
                "item",
                "description",
                true,
                user,
                null
        );

        itemDto = new ItemDto(
                "item",
                "description",
                true,
                null
        );

        request = new Request(
                1L,
                "description",
                user,
                LocalDateTime.now()
        );

        comment = new Comment(
                1L,
                "text",
                item,
                user,
                LocalDateTime.now()
        );

        commentDto = new CommentDto(
                "text"
        );

        lastBooking = new Booking(
                1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item,
                user,
                Status.WAITING
        );

        nextBooking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1),
                item,
                user,
                Status.WAITING
        );

    }

    @Test
    void create_whenItemRequestHasNull_thenSavedItemAndReturnItemRequest() {
        ItemUserRequestIdDto expectedItem = new ItemUserRequestIdDto(
                1L,
                "item",
                "description",
                true,
                user.getId(),
                null
        );
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        try (MockedStatic<ItemMapper> mockedStatic = mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItem(itemDto, user, null)).thenReturn(item);
            mockedStatic.when(() -> ItemMapper.toItemUserRequestIdDto(any())).thenReturn(expectedItem);
        }

        ItemUserRequestIdDto actualItem = itemService.create(user.getId(), itemDto);

        verify(itemRepository, times(1)).save(any());
        assertEquals(actualItem.getOwner(), expectedItem.getOwner());
    }

    @Test
    void create_whenItemRequestHasId_thenSavedItemAndReturnItemRequest() {
        ItemUserRequestIdDto expectedItem = new ItemUserRequestIdDto(
                1L,
                "item",
                "description",
                true,
                user.getId(),
                1L
        );
        ItemDto itemDtoRequest = itemDto;
        itemDtoRequest.setRequestId(1L);
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        try (MockedStatic<ItemMapper> mockedStatic = mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItem(itemDtoRequest, user, request)).thenReturn(item);
            mockedStatic.when(() -> ItemMapper.toItemUserRequestIdDto(any())).thenReturn(expectedItem);
        }

        ItemUserRequestIdDto actualItem = itemService.create(user.getId(), itemDtoRequest);

        verify(itemRepository, times(1)).save(any());
        assertEquals(actualItem.getOwner(), expectedItem.getOwner());
    }

    @Test
    void create_whenCommentIsCorrectAndBookingExists_thenReturnComment() {
        CommentRequestDto commentRequest = new CommentRequestDto(
                comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated()
        );
        when(bookingRepository.existsBookings(any(), any(), any(), any())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);

        try (MockedStatic<CommentMapper> mockedStatic = mockStatic(CommentMapper.class)) {
            mockedStatic.when(() -> CommentMapper.toComment(commentDto, item, user)).thenReturn(comment);
            mockedStatic.when(() -> CommentMapper.toCommentRequestDto(comment)).thenReturn(commentRequest);
        }

        CommentRequestDto actualComment = itemService.create(user.getId(), item.getId(), commentDto);

        verify(commentRepository, times(1)).save(any());
        assertEquals(actualComment.getAuthorName(), commentRequest.getAuthorName());
    }

    @Test
    void create_whenCommentIsCorrectAndBookingNotExists_thenReturnIllegalArgumentExceptionThrow() {
        when(bookingRepository.existsBookings(any(), any(), any(), any())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> itemService.create(user.getId(), item.getId(), commentDto));
    }

    @Test
    void update_whenOwnerInItemMatchUser_thenUpdateItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item actualItem = itemService.update(user.getId(), item.getId(), itemDto);

        verify(itemRepository, times(1)).save(any());
        assertEquals(item.getId(), actualItem.getId());
    }

    @Test
    void update_whenOwnerInItemNoMatchUser_thenNoSuchElementException() {
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        assertThrows(NoSuchElementException.class, () -> itemService.update(3L, item.getId(), itemDto));
    }

    @Test
    void getItem_whenItemExists_thenReturnItem() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "name",
                "description",
                true,
                user,
                null,
                null,
                List.of()
        );
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findNextBooking(any(), any())).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBooking(any(), any())).thenReturn(Optional.of(lastBooking));

        try (MockedStatic<ItemMapper> mockedStatic = mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItemRequest(any(), any(), any(), any())).thenReturn(itemRequestDto);
        }

        ItemRequestDto actualitemRequestDto = itemService.getItem(user.getId(), item.getId());

        assertEquals(actualitemRequestDto.getId(), itemRequestDto.getId());
    }

    @Test
    void getItem_whenItemExistsAndLastBookingIsNull_thenReturnItemWithoutBookings() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "name",
                "description",
                true,
                user,
                null,
                null,
                List.of()
        );
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findNextBooking(any(), any())).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBooking(any(), any())).thenReturn(Optional.empty());

        try (MockedStatic<ItemMapper> mockedStatic = mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItemRequest(any(), any(), any(), any())).thenReturn(itemRequestDto);
        }

        ItemRequestDto actualitemRequestDto = itemService.getItem(user.getId(), item.getId());

        assertEquals(actualitemRequestDto.getId(), itemRequestDto.getId());
    }

    @Test
    void getAll_whenItemExists_thenReturnListOfItem() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "name",
                "description",
                true,
                user,
                null,
                null,
                List.of()
        );
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByOwner(any(), any())).thenReturn(List.of(item));
        when(bookingRepository.findNextBooking(any(), any())).thenReturn(Optional.empty());
        when(bookingRepository.findLastBooking(any(), any())).thenReturn(Optional.empty());

        try (MockedStatic<ItemMapper> mockedStatic = mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItemRequest(any(), any(), any(), any())).thenReturn(itemRequestDto);
        }

        List<ItemRequestDto> actualListOfItemRequestDto = itemService.getAll(user.getId(), null);

        assertEquals(actualListOfItemRequestDto.get(0).getId(), itemRequestDto.getId());
    }

    @Test
    void search_whenItemWithThisTextExists_thenReturnListOfItem() {
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any())).thenReturn(List.of(item));

        List<Item> actualListOfItemRequestDto = itemService.search(user.getId(), "text", null);

        assertEquals(actualListOfItemRequestDto.get(0).getId(), item.getId());
    }

    @Test
    void search_whenTextIsNull_thenReturnEmptyList() {
        List<Item> actualListOfItemRequestDto = itemService.search(user.getId(), null, null);
        assertEquals(actualListOfItemRequestDto.size(), 0);
    }

    @Test
    void compare_whenIdComparisonIsMinus() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L, "name", "description", true, user, null, null, null
        );
        ItemRequestDto itemRequestDto1 = itemRequestDto;
        itemRequestDto1.setId(2L);
        ItemRequestDtoComparator itemRequestDtoComparator = new ItemRequestDtoComparator();
        Integer result = itemRequestDtoComparator.compare(itemRequestDto, itemRequestDto1);
        assertEquals(result, -1);
    }

    @Test
    void compare_whenIdComparisonIsNonZero() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                1L, "name1", "description1", true, user, BookingMapper.toRequest(lastBooking), null, null
        );
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                2L, "name2", "description2", true, user, BookingMapper.toRequest(lastBooking), null, null
        );

        ItemRequestDtoComparator itemRequestDtoComparator = new ItemRequestDtoComparator();
        int result = itemRequestDtoComparator.compare(itemRequestDto1, itemRequestDto2);
        assertTrue(result < 0);

        result = itemRequestDtoComparator.compare(itemRequestDto2, itemRequestDto1);
        assertTrue(result > 0);
    }

    @Test
    void compare_whenLastBookingComparisonIsNonZero() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                1L, "name1", "description1", true, user, BookingMapper.toRequest(lastBooking), null, null
        );
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                1L, "name2", "description2", true, user, BookingMapper.toRequest(nextBooking), null, null
        );

        ItemRequestDtoComparator itemRequestDtoComparator = new ItemRequestDtoComparator();
        int result = itemRequestDtoComparator.compare(itemRequestDto1, itemRequestDto2);
        assertTrue(result != 0);

        result = itemRequestDtoComparator.compare(itemRequestDto2, itemRequestDto1);
        assertTrue(result != 0);
    }

    @Test
    void compare_whenNextBookingComparisonIsNonZero() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                1L, "name1", "description1", true, user, BookingMapper.toRequest(lastBooking), null, null
        );
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                1L, "name2", "description2", true, user, BookingMapper.toRequest(lastBooking), BookingMapper.toRequest(nextBooking), null
        );

        ItemRequestDtoComparator itemRequestDtoComparator = new ItemRequestDtoComparator();
        int result = itemRequestDtoComparator.compare(itemRequestDto1, itemRequestDto2);
        assertTrue(result != 0);

        result = itemRequestDtoComparator.compare(itemRequestDto2, itemRequestDto1);
        assertTrue(result != 0);
    }
}
