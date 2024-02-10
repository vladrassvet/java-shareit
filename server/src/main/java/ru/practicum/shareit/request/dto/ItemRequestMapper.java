package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user, LocalDateTime localDateTime) {

        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(localDateTime)
                .build();
    }

    public static ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest, List<ItemDto> itemDtoList) {

        return ItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtoList)
                .build();
    }
}
