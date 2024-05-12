package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        return userService.update(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable Long userId) {
        userService.remove(userId);
    }
}
