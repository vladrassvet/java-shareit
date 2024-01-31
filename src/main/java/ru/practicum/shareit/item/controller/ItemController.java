package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = itemService.addItem(userId, itemDto);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable long id, @RequestHeader(USER_ID) long userId,
                              @RequestBody ItemDto itemDto) {
        Item item = itemService.updateItem(id, userId, itemDto);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{id}")
    public ItemWithCommentsAndBookings getItemById(@PathVariable long id, @RequestHeader(USER_ID) long userId) {
        return itemService.getItem(id, userId);
    }

    @GetMapping
    public List<ItemWithCommentsAndBookings> getUserAllItems(@RequestHeader(USER_ID) Long userId,
                                                             @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                             @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text,
                                    @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                    @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return itemService.searchItem(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(USER_ID) long userId, @Valid @RequestBody CommentDtoInput commentDtoInput) {
        return CommentMapper.toCommentDto(itemService.addComment(userId, itemId, commentDtoInput));
    }
}
