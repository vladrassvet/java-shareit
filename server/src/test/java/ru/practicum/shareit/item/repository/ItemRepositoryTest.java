package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private Item item;
    private User user;
    private User requestor;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        requestor = User.builder()
                .email("r@mail.ru")
                .name("r")
                .build();
        userRepository.save(requestor);
        request = ItemRequest.builder()
                .requestor(requestor)
                .created(LocalDateTime.now())
                .description("description")
                .build();
        itemRequestRepository.save(request);
        user = User.builder()
                .email("alex@mail.ru")
                .name("alex")
                .build();
        userRepository.save(user);
        item = Item.builder()
                .name("вещь")
                .description("новая вещь")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        itemRepository.save(item);
    }


    @Test
    void findAllByOwnerId() {
        List<Item> itemList = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 5));
        assertEquals(1, itemList.size());
        assertTrue(itemList.contains(item));
    }

    @Test
    void findAllByRequest() {
        List<Item> itemList = itemRepository.findAllByRequest(request);
        assertEquals(1, itemList.size());
        assertEquals("r@mail.ru", itemList.get(0).getRequest().getRequestor().getEmail());
    }

    @Test
    void searchWithPagination() {
        String text = "новая вещь";
        List<Item> itemList = itemRepository.searchWithPagination(text, PageRequest.of(0, 5));
        assertEquals(1, itemList.size());
        assertEquals(text, itemList.get(0).getDescription());
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
