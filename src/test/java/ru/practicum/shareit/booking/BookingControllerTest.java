package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    BookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    ObjectMapper mapper = new ObjectMapper();
    MockMvc mvc;
    BookingDto bookingDto;
    Booking booking;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
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

        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        booking = new Booking(
                1L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                Status.WAITING
        );
    }

    @Test
    void create_whenBookingIsCorrect_thenSavedBooking() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    void update_whenBookingIsExists_thenUpdateBooking() throws Exception {
        when(bookingService.update(1L, 1L, true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    void getBooking_whenBookingIsCorrect_thenSavedBooking() throws Exception {
        when(bookingService.getBooking(any(), any()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    void getAllBookings_whenUserIsCorrect_thenReturnAllBookings() throws Exception {
        when(bookingService.getAllBookings(any(), any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));
    }

    @Test
    void getAllBookingItems_whenUserIsCorrect_thenReturnAllBookingsByItems() throws Exception {
        when(bookingService.getAllBookingItems(any(), any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));
    }
}
