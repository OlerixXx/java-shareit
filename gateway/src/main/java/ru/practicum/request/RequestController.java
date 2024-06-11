package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.groups.Create;
import ru.practicum.request.dto.RequestDto;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Create.class) RequestDto requestDto) {
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllRequestsItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestClient.getAllRequestsItems(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestClient.getRequest(userId, requestId);
    }
}
