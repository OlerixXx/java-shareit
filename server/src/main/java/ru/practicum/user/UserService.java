package ru.practicum.user;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    User create(UserDto userDto);

    User update(UserDto userDto, Long userId);

    User getUser(Long userId);

    List<User> getAll();

    void remove(Long userId);
}
