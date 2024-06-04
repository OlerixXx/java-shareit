package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);

        user = new User(
                1L,
                "user",
                "email@mail.ru",
                LocalDateTime.now()
        );

        userDto = new UserDto(
                "user",
                "email@mail.ru"
        );
    }

    @Test
    void remove_whenUserExists_thenRemoveUse() {
        userService.remove(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateUser_whenUserNotFound_thenReturnNoSuchElementExceptionThrow() {
        User newUser = new User(user.getId(), "newUser", null, LocalDateTime.now());
        when(userRepository.findById(1L)).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> userService.update(newUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_whenUserFound_thenUpdateOnlyEmail() {
        User newUser = new User(user.getId(), "newUser", null, LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User actualUser = userService.update(newUser);

        verify(userRepository).save(any(User.class));
        assertEquals(newUser.getName(), actualUser.getName());
        assertEquals(newUser.getEmail(), user.getEmail());
        assertEquals(newUser.getRegistrationDate(), actualUser.getRegistrationDate());
    }

    @Test
    void updateUser_whenUserFound_thenUpdateOnlyName() {
        User newUser = new User(user.getId(), null, "newemail@mail.ru", LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User actualUser = userService.update(newUser);

        verify(userRepository).save(any(User.class));
        assertEquals(newUser.getName(), user.getName());
        assertEquals(newUser.getEmail(), actualUser.getEmail());
        assertEquals(newUser.getRegistrationDate(), actualUser.getRegistrationDate());
    }

    @Test
    void updateUser_whenUserFound_thenUpdateAllFields() {
        User newUser = new User(user.getId(), "newUser", "newemail@mail.ru", LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User actualUser = userService.update(newUser);

        verify(userRepository).save(any(User.class));

        assertEquals(newUser.getName(), actualUser.getName());
        assertEquals(newUser.getEmail(), actualUser.getEmail());
        assertEquals(newUser.getRegistrationDate(), actualUser.getRegistrationDate());
    }

    @Test
    void create_whenUserIsCorrect_thenSavedUser() {
        User userToSave = user;
        when(userRepository.save(user)).thenReturn(userToSave);

        User actualUser = userService.create(userToSave);

        assertEquals(actualUser, userToSave);
        verify(userRepository).save(userToSave);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        userService.create(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User actualUser = userService.getUser(user.getId());

        assertEquals(user, actualUser);
    }

    @Test
    void getUser_whenUserNotFound_thenReturnNoSuchElementExceptionThrow() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUser(user.getId()));
    }

    @Test
    void getAll_whenUsersFound_thenReturnUsersList() {
        userService.create(user);
        List<User> expectedList = new ArrayList<>(List.of(user));
        when(userRepository.findAll()).thenReturn(expectedList);

        List<User> actualList = userService.getAll();

        assertEquals(actualList, expectedList);
    }
}
