package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(long userId, ItemDto itemDto);

    Item updateItem(long id, long userId, ItemDto item);

    ItemWithCommentsAndBookings getItem(long id, long userId);

    List<ItemWithCommentsAndBookings> getUserItems(Long userId);

    List<Item> searchItem(String text);

    Comment addComment(long userId, long itemId, CommentDtoInput commentDtoInput);
}
