package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    UserService userService;
    @InjectMocks
    UserController userController;
    @InjectMocks
    ErrorHandler errorHandler;

    ObjectMapper mapper = new ObjectMapper();
    MockMvc mvc;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(errorHandler)
                .build();

        userDto = new UserDto(
                "user",
                "email@mail.ru"
        );

        user = new User(
                1L,
                "user",
                "email@mail.ru",
                LocalDateTime.now()
        );
    }

    @Test
    void create_whenUserIsCorrect_thenSavedUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void update_whenUserFound_thenUpdateAllFields() throws Exception {
        User newUser = new User(user.getId(), "newuser", "newemail@mail.ru", LocalDateTime.now());
        UserDto newUserDto = new UserDto(newUser.getName(), newUser.getEmail());
        when(userService.update(any()))
                .thenReturn(newUser);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(newUser.getName())))
                .andExpect(jsonPath("$.email", is(newUser.getEmail())));
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getUser_whenUserNotFound_thenReturn404() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(NoSuchElementException.class);

        mvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_whenUserFound_thenReturnListNotEmpty() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(user));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));
    }

    @Test
    void remove_whenUserExists_thenRemoveUse() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).remove(1L);
    }

    @Test
    void create_whenInvalidInput_thenReturns400() throws Exception {
        UserDto userDto = new UserDto(
                null,
                null
        );

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }
}
