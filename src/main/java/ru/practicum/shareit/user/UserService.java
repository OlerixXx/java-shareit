package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User update(User user);

    User getUser(Long userId);

    List<User> getAll();

    void remove(Long userId);
}
