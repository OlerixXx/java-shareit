package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.create(user);
    }

    public User update(User user) {
        return userRepository.update(user);
    }

    public User getUser(Long userId) {
        return userRepository.getUser(userId);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void remove(Long userId) {
        userRepository.remove(userId);
    }
}
