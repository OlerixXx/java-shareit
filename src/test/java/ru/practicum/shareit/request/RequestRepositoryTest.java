package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RequestRepository requestRepository;

    Request testRequest;
    User testUser;

    @BeforeEach
    public void setUp() {
        // Initialize test data before each test method
        testUser = new User(
                1L,
                "name",
                "email@email.ru",
                LocalDateTime.now()
        );

        testRequest = new Request(
                1L,
                "description",
                testUser,
                LocalDateTime.now()
        );

        userRepository.save(testUser);
        requestRepository.save(testRequest);
    }

    @AfterEach
    public void tearDown() {
        // Release test data after each test method
        userRepository.delete(testUser);
        requestRepository.delete(testRequest);
    }

    @Test
    void findAllExcludingIOwner_whenRequestorIsExists_thenReturnListWithoutOwner() {
        List<Request> savedRequest = requestRepository.findAllExcludingOwner(1L, null);
        assertNotNull(savedRequest);
    }

}
