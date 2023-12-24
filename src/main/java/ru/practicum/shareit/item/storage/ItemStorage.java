package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    List<Item> getAllUserItem(long userId);

    Item updateItem(long userId, Item item);

    List<Item> searchingForItem(String text);

    Item getItemById(long id);

}
