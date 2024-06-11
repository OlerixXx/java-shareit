package ru.practicum.request.mapper;

import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Item;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestWithItemsDto;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapper {

    public static Request toRequest(RequestDto requestDto, User user) {
        return new Request(
                null,
                requestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
    }

    public static RequestWithItemsDto toRequestWithItems(Request request, List<Item> items) {
        return new RequestWithItemsDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated(),
                ItemMapper.toListItemRequestWithoutUserDto(items)
        );
    }


}
