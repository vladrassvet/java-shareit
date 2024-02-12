package ru.practicum.shareit.items;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.comment.CommentDtoInput;
import ru.practicum.shareit.items.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader(USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("пользователь с userId {}, добавил новую вещь {} .", userId, itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable long id, @RequestHeader(USER_ID) long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("у пользователя с userId {}, обновлены данные вещи с id {}, на {}", userId, id, itemDto);
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable long id, @RequestHeader(USER_ID) long userId) {
        log.info("вернули по запросу вещь с id {}, пользователя с userId {} (комментариями, если они были)", id, userId);
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserAllItems(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                  @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        log.info("вернули список вещей пользователя userId {}, начиная с элемента from {}, количеством выведенного size {}",
                userId, from, size);
        return itemClient.getUserAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_ID) Long userId,
                                             @RequestParam(name = "text") String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("найдены вещи, в названии или описании которых присутствует слово text {}", text);
        return itemClient.findItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @RequestHeader(USER_ID) long userId, @Valid @RequestBody CommentDtoInput commentDtoInput) {
        log.info("пользователем с userId {}, добавлен комментарий {}, к вещи с itemId {}", userId, commentDtoInput, itemId);
        return itemClient.addComment(itemId, userId, commentDtoInput);
    }
}
