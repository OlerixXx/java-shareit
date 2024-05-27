package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        User oldUser = userRepository.findById(user.getId()).orElseThrow(NoSuchElementException::new);
        if (!StringUtils.hasText(user.getEmail())) {
            user.setEmail(oldUser.getEmail());
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(oldUser.getName());
        }
        return userRepository.save(user);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void remove(Long userId) {
        userRepository.deleteById(userId);
    }
}
