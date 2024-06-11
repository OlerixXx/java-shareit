package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEmailContaining(String email);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM users WHERE id=?1)", nativeQuery = true)
    boolean userExists(Long userId);
}