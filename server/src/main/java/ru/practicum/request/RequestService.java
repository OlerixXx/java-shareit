package ru.practicum.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestWithItemsDto;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {
    Request create(Long userId, RequestDto requestDto);

    List<RequestWithItemsDto> getAllRequests(Long userId);

    List<RequestWithItemsDto> getAllRequestsItems(Long userId, Pageable page);

    RequestWithItemsDto getRequest(Long userId, Long requestId);
}
