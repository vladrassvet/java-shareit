package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest addRequest(@RequestHeader(USER_ID) long userId,
                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        return itemRequestService.addRequest(userId, itemRequestDto, now);
    }

    @GetMapping
    public List<ItemRequestResponse> getAllMyRequests(@RequestHeader(USER_ID) long userId) {
        return itemRequestService.getAllMyRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getListOfOtherUsersRequests(@RequestHeader(USER_ID) long userId,
                                                                 @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {

        return itemRequestService.getListOfOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getItemRequest(@PathVariable long requestId, @RequestHeader(USER_ID) long userId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }
}
