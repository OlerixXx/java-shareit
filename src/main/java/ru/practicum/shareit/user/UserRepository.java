package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User update(User user);

    User getUser(Long userId);

    List<User> getAll();

    void remove(Long userId);

    boolean isExist(Long userId);

}
