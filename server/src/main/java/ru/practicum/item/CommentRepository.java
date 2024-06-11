package ru.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem(Item item);
}
