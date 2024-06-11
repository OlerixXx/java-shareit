package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.*;
import ru.practicum.item.model.Item;
import ru.practicum.pageable.ConvertPageable;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemUserRequestIdDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return itemService.create(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemRequestDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam Integer from,
                                       @RequestParam Integer size) {
        return itemService.getAll(userId, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/search")
    public List<Item> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam("text") String text,
                             @RequestParam Integer from,
                             @RequestParam Integer size) {
        return itemService.search(userId, text, ConvertPageable.toMakePage(from, size));
    }
}
