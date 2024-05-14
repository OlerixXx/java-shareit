package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

public interface UserService {

    User create(User user);

    User update(User user);

    User getUser(Long userId);

    List<User> getAll();

    void remove(Long userId);
}
