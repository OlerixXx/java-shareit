package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public User create(UserDto userDto) {
        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Transactional
    public User update(UserDto userDto, Long userId) {
        User user = UserMapper.toUpdateUser(userDto, userRepository.findById(userId).orElseThrow(NoSuchElementException::new));
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
