package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long userId, Pageable pageable);

    List<Item> findAllByRequest(ItemRequest request);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (lower(i.name) LIKE lower(concat('%', :text,'%')) " +
            "OR lower(i.description) LIKE lower(concat('%', :text,'%'))) " +
            "AND i.available=TRUE")
    List<Item> searchWithPagination(String text, Pageable pageable);
}
