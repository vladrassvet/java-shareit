package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private User userOwner;
    private User userBooker;

    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        userOwner = User.builder()
                .id(1L)
                .email("userOwner@yandex.ru")
                .name("userOwner")
                .build();

        userBooker = User.builder()
                .id(2L)
                .email("userBooker@yandex.ru")
                .name("userBooker")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Очень хорошая дрель")
                .available(true)
                .owner(userOwner)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("itemRequest")
                .requestor(userOwner)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addRequest_whenRequestFound_thenReturnRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("запрос на дрель", 2);
        ItemRequest itemRequest = new ItemRequest(1, "!!!", userBooker, now);
        when(userRepository.findById(userBooker.getId()))
                .thenReturn(Optional.of(userBooker));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequest itemRequest1 = itemRequestService.addRequest(userBooker.getId(), itemRequestDto, now);
        assertEquals(userBooker.getId(), itemRequest1.requestor.getId(), "Неверно отработал метод");
        assertEquals("!!!", itemRequest1.getDescription(), "Неверно отработал метод");
        assertEquals(now, itemRequest1.getCreated());
    }

    @Test
    void addRequest_whenRequestNotFound_thenReturnThrows() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("запрос на дрель", 2);
        when(userRepository.findById(userBooker.getId()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(userBooker.getId(), itemRequestDto, now));
    }

    @Test
    void getAllMyRequests_whenUserIdValid_thenReturnItemRequestResponse() {
        List<ItemRequest> list = List.of(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(any(User.class))).thenReturn(list);
        List<ItemRequestResponse> itemRequestList = itemRequestService.getAllMyRequests(userOwner.getId());
        assertFalse(itemRequestList.isEmpty());
        assertEquals(list.get(0).getId(), itemRequestList.get(0).getId(), "Некорректно отработал метод");
        assertEquals(list.get(0).getDescription(), itemRequestList.get(0).getDescription(), "Некорректно отработал метод");
        assertEquals(list.get(0).getCreated(), itemRequestList.get(0).getCreated(), "Некорректно отработал метод");
    }


    @Test
    void getAllMyRequests_whenUserIdNotValid_thenReturnThrows() {
        when(userRepository.findById(userOwner.getId()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllMyRequests(userOwner.getId()));
    }

    @Test
    void getListOfOtherUsersRequests_whenInputValueValid_thenReturnItemRequestResponse() {
        List<ItemRequest> list = List.of(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(any(User.class), any(Pageable.class)))
                .thenReturn(list);
        List<ItemRequestResponse> itemRequestResponseList = itemRequestService.getListOfOtherUsersRequests(userOwner.getId(), 1, 5);
        assertFalse(itemRequestResponseList.isEmpty());
        assertEquals(list.get(0).getId(), itemRequestResponseList.get(0).getId(), "Некорректно отработал метод");
        assertEquals(list.get(0).getDescription(), itemRequestResponseList.get(0).getDescription(), "Некорректно отработал метод");
        assertEquals(list.get(0).getCreated(), itemRequestResponseList.get(0).getCreated(), "Некорректно отработал метод");
    }

    @Test
    void getListOfOtherUsersRequests_whenInputValueNotValid_thenReturnItemRequestTrows() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getListOfOtherUsersRequests(userOwner.getId(), 1, 5));
    }

    @Test
    void getItemRequest_whenInputValueValid_thenReturnItemRequestResponse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequestResponse itemRequestResponse = itemRequestService.getItemRequest(itemRequest.getId(), userOwner.getId());
        assertTrue(itemRequestResponse != null);
        assertEquals(itemRequest.getId(), itemRequestResponse.getId(), "некорректно отработал метод");
        assertEquals(itemRequest.getCreated(), itemRequestResponse.getCreated(), "некорректно отработал метод");
        assertEquals(itemRequest.getDescription(), itemRequestResponse.getDescription(), "некорректно отработал метод");
    }

    @Test
    void getItemRequest_whenUseridNotValid_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(itemRequest.getId(), userOwner.getId()));
    }

    @Test
    void getItemRequest_whenRequestIdNotValid_thenReturnThrows() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(itemRequest.getId(), userOwner.getId()));
    }
}
