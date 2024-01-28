package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private User userOwner;
    private User requestor;

    private Item item;


    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDto1;

    @BeforeEach
    void setUp() {
        userOwner = User.builder()
                .email("userOwner@yandex.ru")
                .name("userOwner")
                .build();
        userRepository.save(userOwner);
        requestor = User.builder()
                .email("userBooker@yandex.ru")
                .name("userBooker")
                .build();
        userRepository.save(requestor);
        item = Item.builder()
                .name("Дрель")
                .description("Очень хорошая дрель")
                .available(true)
                .owner(userOwner)
                .build();
        itemRepository.save(item);
        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requestor(requestor.getId())
                .build();
        itemRequestDto1 = ItemRequestDto.builder()
                .description("new description")
                .requestor(requestor.getId())
                .build();
    }

    @Test
    void addRequest() {
        ItemRequest itemRequest1 = itemRequestService.addRequest(requestor.getId(), itemRequestDto, now);
        assertEquals("description", itemRequest1.getDescription());
    }

    @Test
    void getAllMyRequests() {
        itemRequestService.addRequest(requestor.getId(), itemRequestDto, now);
        itemRequestService.addRequest(requestor.getId(), itemRequestDto1, now);
        List<ItemRequestResponse> responseList = itemRequestService.getAllMyRequests(requestor.getId());
        assertEquals(2, responseList.size());
    }

    @Test
    void getListOfOtherUsersRequests() {
        itemRequestService.addRequest(requestor.getId(), itemRequestDto, now);
        itemRequestService.addRequest(requestor.getId(), itemRequestDto1, now);
        List<ItemRequestResponse> responseList = itemRequestService.getListOfOtherUsersRequests(userOwner.getId(), 0, 1);
        assertEquals(1, responseList.size());
    }

    @Test
    void getItemRequest() {
        ItemRequest itemRequest1 = itemRequestService.addRequest(requestor.getId(), itemRequestDto, now);
        ItemRequest itemRequest2 = itemRequestService.addRequest(requestor.getId(), itemRequestDto1, now);
        ItemRequestResponse itemRequestResponse = itemRequestService.getItemRequest(itemRequest1.getId(), requestor.getId());
        assertEquals("description", itemRequestResponse.getDescription());
    }
}
