package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item.getRequest() == null) {
            item.setRequest(new ItemRequest());
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    public static Item toItem(User user, ItemDto itemDto, ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .request(request)
                .build();
    }

    public static Item toItemWithId(long id, User user, ItemDto itemDto) {
        return Item.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

    public static ItemWithCommentsAndBookings toItemWithTime(Item item, BookingShort last, BookingShort next,
                                                             List<CommentDto> commentDto) {
        return ItemWithCommentsAndBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(commentDto)
                .build();
    }
}
