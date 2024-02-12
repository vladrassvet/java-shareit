package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private User user;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private Item item;

    private static String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("запрос")
                .created(LocalDateTime.now())
                .requestor(new User())
                .build();
        user = User.builder()
                .id(1L)
                .email("alex@yandex.ru")
                .name("alex")
                .build();
        item = Item.builder()
                .id(1L)
                .name("стул")
                .description("удобный стул")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.toItemDto(item);
    }

    @SneakyThrows
    @Test
    void addNewItem_whenInputValueValid_thenReturnSaveItemDto() {
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
        verify(itemService, times(1)).addItem(user.getId(), itemDto);
    }

    @SneakyThrows
    @Test
    void addItem_whenInputValueNotValid_thenReturnThrows() {
        Item item1 = Item.builder()
                .id(2L)
                .name("")
                .description("")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        /* mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());*/
    }

    @SneakyThrows
    @Test
    void updateItem_whenInputValueValid_thenReturnUpdateItemDto() {
        when(itemService.updateItem(item.getId(), user.getId(), itemDto)).thenReturn(item);
        mockMvc.perform(patch("/items/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @SneakyThrows
    @Test
    void getItemById_whenInputValueValid_thenReturnItem() {
        BookingShort last = BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .build();
        BookingShort next = BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .build();
        List<CommentDto> comments = new ArrayList<>();
        ItemWithCommentsAndBookings andBookings = ItemMapper.toItemWithTime(item, last, next, comments);

        when(itemService.getItem(item.getId(), user.getId())).thenReturn(andBookings);

        mockMvc.perform(get("/items/{id}", item.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(andBookings.getAvailable()))
                .andExpect(jsonPath("$.name").value(andBookings.getName()));
    }

    @SneakyThrows
    @Test
    void searchItem_whenInputValueValid_thenReturnListItemSearsh() {
        List<Item> itemList = List.of(item);

        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemList);

        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1")
                        .param("text", "удобный стул")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value(itemList.get(0).getDescription()))
                .andExpect(jsonPath("$[0].name").value(itemList.get(0).getName()));
    }

    @SneakyThrows
    @Test
    void getUserAllItems() {
        BookingShort last = BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .build();
        BookingShort next = BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .build();
        List<CommentDto> comments = new ArrayList<>();
        ItemWithCommentsAndBookings andBookings1 = ItemMapper.toItemWithTime(item, last, next, comments);
        List<ItemWithCommentsAndBookings> andBookings = List.of(andBookings1);

        when(itemService.getUserItems(anyLong(), anyInt(), anyInt())).thenReturn(andBookings);

        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1")
                        .param("from", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(andBookings.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(andBookings.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(andBookings.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].id").value(andBookings.get(0).getId()));
    }

    @SneakyThrows
    @Test
    void addComment_whenInputValueValid_thenReturnSaveComment() {
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("comment")
                .build();
        Comment comment = CommentMapper.toComment(commentDtoInput, item, user);

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDtoInput.class)))
                .thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentDtoInput))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @SneakyThrows
    @Test
    void addComment_whenInputValueNotValid_thenReturnThrows() {
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("")
                .build();
        Comment comment = CommentMapper.toComment(commentDtoInput, item, user);

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDtoInput.class)))
                .thenReturn(comment);
        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentDtoInput))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}