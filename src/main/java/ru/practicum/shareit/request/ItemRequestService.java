package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {

    ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto, LocalDateTime localDateTime);

    List<ItemRequestResponse> getAllMyRequests(long userId);

    List<ItemRequestResponse> getListOfOtherUsersRequests(long userId, Integer from, Integer size);

    ItemRequestResponse getItemRequest(long requestId, long userId);
}
