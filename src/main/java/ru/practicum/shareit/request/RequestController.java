package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.pageable.ConvertPageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public Request create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Create.class) RequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<RequestWithItemsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping(value = "/all")
    public List<RequestWithItemsDto> getAllRequestsItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestService.getAllRequestsItems(userId, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/{requestId}")
    public RequestWithItemsDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestService.getRequest(userId, requestId);
    }
}
