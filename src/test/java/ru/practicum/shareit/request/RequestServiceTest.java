package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pageable.ConvertPageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;

    RequestService requestService;

    private User user;
    private Item item;
    private Request request;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(userRepository, itemRepository, requestRepository);

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

        request = new Request(
                1L,
                "description",
                user,
                LocalDateTime.now()
        );

        requestDto = new RequestDto(
                "description"
        );
    }

    @Test
    void create_whenRequestAvailableIsTrue_thenSavedRequest() {
        Request requestToSave = request;
        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(requestToSave);

        Request actualBooking = requestService.create(user.getId(), requestDto);

        assertEquals(actualBooking, requestToSave);
    }

    @Test
    void create_whenUserNotExists_thenNoSuchElementExceptionThrow() {
        when(userRepository.userExists(user.getId())).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> requestService.create(user.getId(), requestDto));
    }

    @Test
    void getAllRequests_whenAllByRequestorId_thenReturnRequestsList() {
        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(request));

        List<RequestWithItemsDto> actualBooking = requestService.getAllRequests(user.getId());

        assertEquals(actualBooking.size(), 1);
        assertEquals(actualBooking.get(0).getId(), request.getId());
    }

    @Test
    void getAllRequestsItems_whenAllExcludingIOwner_thenReturnRequestsList() {
        when(requestRepository.findAllExcludingOwner(any(), any())).thenReturn(List.of(request));

        List<RequestWithItemsDto> actualBooking = requestService.getAllRequestsItems(user.getId(), ConvertPageable.toMakePage(0, 10));

        assertEquals(actualBooking.size(), 1);
        assertEquals(actualBooking.get(0).getId(), request.getId());
    }

    @Test
    void getAllRequests_whenUserNotExists_thenNoSuchElementExceptionThrow() {
        when(userRepository.userExists(user.getId())).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> requestService.getAllRequests(user.getId()));
    }

    @Test
    void getAllRequests_whenRequestsIsEmpty_thenReturnEmptyList() {
        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of());

        List<RequestWithItemsDto> actualBooking = requestService.getAllRequests(user.getId());

        assertEquals(actualBooking.size(), 0);
    }

    @Test
    void getRequest_whenRequestIdExisting_thenReturnRequest() {
        Request requestToSave = request;
        when(userRepository.userExists(user.getId())).thenReturn(true);
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(any())).thenReturn(List.of(item));

        RequestWithItemsDto actualBooking = requestService.getRequest(user.getId(), request.getId());

        assertEquals(actualBooking.getId(), requestToSave.getId());
    }

    @Test
    void getRequest_whenUserNotExists_thenNoSuchElementExceptionThrow() {
        when(userRepository.userExists(user.getId())).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> requestService.getRequest(user.getId(), null));
    }
}
