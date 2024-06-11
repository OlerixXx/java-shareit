package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.pageable.ConvertPageable;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestWithItemsDto;
import ru.practicum.request.model.Request;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public Request create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody RequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<RequestWithItemsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping(value = "/all")
    public List<RequestWithItemsDto> getAllRequestsItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam Integer from,
                                             @RequestParam Integer size) {
        return requestService.getAllRequestsItems(userId, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/{requestId}")
    public RequestWithItemsDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestService.getRequest(userId, requestId);
    }
}
