package ru.practicum.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(
                null,
                commentDto.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }

    public static CommentRequestDto toCommentRequestDto(Comment comment) {
        return new CommentRequestDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentRequestDto> toListCommentRequestDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentRequestDto)
                .collect(Collectors.toList());
    }
}
