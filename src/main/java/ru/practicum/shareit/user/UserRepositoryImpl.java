package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new TreeMap<>();
    private Long lastId = 0L;

    @Override
    public User create(User user) {
        if (emailExists(user.getEmail(), user.getId())) {
            throw new EmailAlreadyExistsException();
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.computeIfPresent(user.getId(), (userId, userInMap) -> {
            user.setName(Optional.ofNullable(user.getName()).orElse(userInMap.getName()));
            String newEmail = Optional.ofNullable(user.getEmail()).orElse(userInMap.getEmail());
            if (emailExists(user.getEmail(), userId)) {
                throw new EmailAlreadyExistsException();
            } else {
                user.setEmail(newEmail);
            }
            return user;
        });
        return user;
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void remove(Long userId) {
        users.remove(userId);
    }

    private Long generateId() {
        lastId++;
        return lastId;
    }

    private boolean emailExists(String email, Long userId) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && !user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

}