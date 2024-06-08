package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pageable.ConvertPageable;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemUserRequestIdDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Create.class) ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody @Validated(Create.class) CommentDto commentDto) {
        return itemService.create(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Update.class) ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemRequestDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                       @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemService.getAll(userId, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/search")
    public List<Item> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam("text") String text,
                             @RequestParam(required = false, defaultValue = "0") Integer from,
                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemService.search(userId, text, ConvertPageable.toMakePage(from, size));
    }
}
