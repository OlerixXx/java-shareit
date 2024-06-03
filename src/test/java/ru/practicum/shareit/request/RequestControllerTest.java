package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    @Mock
    RequestService requestService;
    @InjectMocks
    RequestController requestController;
    ObjectMapper mapper = new ObjectMapper();
    MockMvc mvc;
    Request request;
    RequestDto requestDto;
    RequestWithItemsDto requestWithItemsDto;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
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

        request = new Request(
                1L,
                "description",
                user,
                LocalDateTime.now()
        );

        requestDto = new RequestDto(
                "description"
        );

        requestWithItemsDto = new RequestWithItemsDto(
                1L,
                "description",
                user,
                LocalDateTime.now(),
                List.of()
        );
    }

    @Test
    void create_whenRequestIsCorrect_thenCreateRequest() throws Exception {
        when(requestService.create(anyLong(), any()))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class));
    }

    @Test
    void getAllRequests_whenRequestExists_thenReturnRequestList() throws Exception {
        when(requestService.getAllRequests(anyLong()))
                .thenReturn(List.of(requestWithItemsDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequestsItems_whenRequestExists_thenReturnRequestList() throws Exception {
        when(requestService.getAllRequestsItems(anyLong(), any()))
                .thenReturn(List.of(requestWithItemsDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequest_whenRequestExists_thenReturnRequest() throws Exception {
        when(requestService.getRequest(anyLong(), anyLong()))
                .thenReturn(requestWithItemsDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class));
    }
}
