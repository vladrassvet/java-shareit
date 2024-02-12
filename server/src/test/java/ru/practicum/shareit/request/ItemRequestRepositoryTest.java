package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    private User user1;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("alex@mail.ru")
                .name("alex")
                .build();
        userRepository.save(user);
        user1 = User.builder()
                .email("qwerty@mail.ru")
                .name("name")
                .build();
        userRepository.save(user1);
        itemRequest = ItemRequest.builder()
                .description("a description")
                .created(now)
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest);
        itemRequest1 = ItemRequest.builder()
                .description("b description")
                .created(now.minusHours(1))
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest1);
        itemRequest2 = ItemRequest.builder()
                .description("c description")
                .created(now.minusHours(2))
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest2);
    }


    @Test
    void findAllByRequestorOrderByCreatedDesc() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(user);
        assertEquals(3, list.size(), "Метод отработал не корректно");
        assertEquals(itemRequest2, list.get(2), "Метод отработал не корректно");
        assertEquals(itemRequest, list.get(0), "Метод отработал не корректно");
        assertEquals(itemRequest1, list.get(1), "Метод отработал не корректно");
    }

    @Test
    void findAllByRequestorNotOrderByCreatedDesc() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user1, PageRequest.of(0, 2));
        List<ItemRequest> list1 = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user1, PageRequest.of(0, 5));
        assertEquals(2, list.size(), "Метод отработал не корректно");
        assertEquals(3, list1.size(), "Метод отработал не корректно");
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
