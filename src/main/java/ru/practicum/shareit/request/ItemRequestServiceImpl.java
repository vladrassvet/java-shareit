package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto, LocalDateTime localDateTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user, localDateTime);
        log.info("Добавлен новый запрос {}", itemRequest);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestResponse> getAllMyRequests(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        return itemRequestRepository.findAllByRequestorOrderByCreatedDesc(user)
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestResponse(itemRequest, findItems(itemRequest)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getListOfOtherUsersRequests(long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        Pageable page = PageRequest.of(from, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user, page);
        log.info("получен список запросов, созданных другими пользователями {}", requests);
        return requests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestResponse(itemRequest, findItems(itemRequest)))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse getItemRequest(long requestId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с таким id = " + requestId + "  не существует"));
        return ItemRequestMapper.toItemRequestResponse(itemRequest, findItems(itemRequest));
    }

    private List<ItemDto> findItems(ItemRequest request) {
        return itemRepository.findAllByRequest(request).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }
}
