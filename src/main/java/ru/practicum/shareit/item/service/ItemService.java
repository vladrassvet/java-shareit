package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(long userId, ItemDto itemDto);

    Item updateItem(long id, long userId, ItemDto item);

    Item getItem(long id, long userId);

    List<Item> getUserItems(long userId);

    List<Item> searchItem(String text);
}
