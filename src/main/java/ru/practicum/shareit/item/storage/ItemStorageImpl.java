package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoDataRequestedInStorageException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public Item create(Item item) {
        item.setId(++itemId);
        log.info("Добавлена новая вещь - {}", item);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> getAllUserItem(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(long userId, Item item) {
        if (!items.containsKey(item.getId())) {
            log.info("Неправильно передан  id = " + item.getId() + " вещи");
            throw new NoDataRequestedInStorageException("Неправильно передан  id = " + item.getId() + " вещи");
        }
        if (userId != item.getOwner().getId()) {
            log.info("Заданы неверные параметры для обновления");
            throw new ValidationException("Заданы неверные параметры для обновления");
        }
        if (item.getName() != null) {
            items.get(item.getId()).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(item.getId()).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(item.getId()).setAvailable(item.getAvailable());
        }
        log.info("Данные вещи обновлены - {}", items.get(item.getId()));
        return items.get(item.getId());
    }

    @Override
    public List<Item> searchingForItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long id) {
        if (!items.containsKey(id)) {
            log.info("Не найдена вещь с таким id = " + id);
            throw new NoDataRequestedInStorageException("Не найдена вещь с таким id = " + id);
        }
        log.info("Найдена вещь - {}", items.get(itemId));
        return items.get(itemId);
    }
}
