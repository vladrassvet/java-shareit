package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
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
    public ItemDto getItemById(@PathVariable long id, @RequestHeader(USER_ID) long userId) {
        Item item = itemService.getItem(id, userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getUserAllItems(@RequestHeader(USER_ID) long userId) {
        return itemService.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        return itemService.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
