package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID) long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("пользователь userId {}, добавил новый запрос itemRequestDto {}", userId, itemRequestDto);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllMyRequests(@RequestHeader(USER_ID) long userId) {
        return itemRequestClient.getAllMyRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getListOfOtherUsersRequests(@RequestHeader(USER_ID) long userId,
                                                              @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                              @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {

        return itemRequestClient.getListOfOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable long requestId, @RequestHeader(USER_ID) long userId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
