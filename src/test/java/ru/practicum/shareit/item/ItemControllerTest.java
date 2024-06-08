package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController itemController;
    ObjectMapper mapper = new ObjectMapper();
    MockMvc mvc;
    ItemDto itemDto;
    ItemUserRequestIdDto itemUserRequestIdDto;
    CommentRequestDto commentRequestDto;
    ItemRequestDto itemRequestDto;
    BookingRequestDto lastBooking;
    BookingRequestDto nextBooking;
    CommentDto commentDto;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

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
                "name",
                "description",
                true,
                5L
        );

        itemUserRequestIdDto = new ItemUserRequestIdDto(
                1L,
                "name",
                "description",
                true,
                1L,
                5L
        );

        commentRequestDto = new CommentRequestDto(
                1L,
                "text",
                "authorName",
                LocalDateTime.now()
        );

        itemRequestDto = new ItemRequestDto(
                1L,
                "name",
                "description",
                true,
                user,
                lastBooking,
                nextBooking,
                List.of(commentRequestDto)

        );

        lastBooking = new BookingRequestDto(
                1L,
                2L
        );

        nextBooking = new BookingRequestDto(
                1L,
                3L
        );

        commentDto = new CommentDto(
                "text"
        );
    }

    @Test
    void create_whenItemIsCorrect_thenCreateItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemUserRequestIdDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUserRequestIdDto.getId()), Long.class));
    }

    @Test
    void create_whenCommentIsCorrect_thenCreateComment() throws Exception {
        when(itemService.create(anyLong(), any(), any()))
                .thenReturn(commentRequestDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUserRequestIdDto.getId()), Long.class));
    }

    @Test
    void update_whenItemIsCorrectAndExists_thenUpdateItem() throws Exception {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setAvailable(false);

        Item newItem = item;
        newItem.setAvailable(false);

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(newItem);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUserRequestIdDto.getId()), Long.class));
    }

    @Test
    void getItem_whenItemIsExists_thenReturnItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUserRequestIdDto.getId()), Long.class));
    }

    @Test
    void getAll_whenItemIsExists_thenReturnItemList() throws Exception {
        when(itemService.getAll(anyLong(), any()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void search_whenItemIsExists_thenReturnItem() throws Exception {
        when(itemService.search(anyLong(), any(), any()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дРелЬ")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
