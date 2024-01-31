package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequest;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID = "X-Sharer-User-Id";
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResp bookingDtoResp;
    private Item item;
    private User bookingUser;
    private User user;
    private ItemRequest itemRequest;
    private ItemDto itemDto;

    private ItemRequest itemRequestBuilder() {
        ItemRequest itReq = ItemRequest.builder()
                .id(1L)
                .description("запрос")
                .created(LocalDateTime.now())
                .requestor(new User())
                .build();
        return itReq;
    }

    private User bookingUserBuilder() {
        User us = User.builder()
                .id(1L)
                .email("alex@yandex.ru")
                .name("alex")
                .build();
        return us;
    }

    private User userBuilder() {
        User us = User.builder()
                .id(2L)
                .email("alexs@yandex.ru")
                .name("alexs")
                .build();
        return us;
    }

    private Item itemBuilder() {
        Item it = Item.builder()
                .id(1L)
                .name("новый стул")
                .description("удобный стул")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        return it;
    }

    private BookingDtoRequest bookingDtoRequestBuilder() {
        BookingDtoRequest data = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        return data;
    }

    @BeforeEach
    void setUp() {
        itemRequest = itemRequestBuilder();
        bookingUser = bookingUserBuilder();
        user = userBuilder();
        item = itemBuilder();
        itemDto = ItemMapper.toItemDto(item);
        bookingDtoRequest = bookingDtoRequestBuilder();
        booking = BookingMapper.toBooking(bookingDtoRequest, item, bookingUser, BookingStatus.WAITING);
        bookingDtoResp = BookingMapper.toResponse(booking);
    }

    @SneakyThrows
    @Test
    void addBooking_whenInputValueValid_thenReturnSaveBooking() {
        when(bookingService.addBooking(anyLong(), any(BookingDtoRequest.class)))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void addBooking_whenInputValueNotValid_thenReturnThrows() {
        bookingDtoRequest.setEnd(LocalDateTime.now().minusDays(1));
        when(bookingService.addBooking(anyLong(), any(BookingDtoRequest.class)))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).addBooking(anyLong(), any(BookingDtoRequest.class));
    }

    @SneakyThrows
    @Test
    void confirmationOrRejectionOfBooking() {
        when(bookingService.getStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingUser.getId())
                        .header(USER_ID, "1")
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.start").value(String.valueOf(bookingDtoResp.getStart())));
    }

    @SneakyThrows
    @Test
    void getBooking_whenIhputValueValid_thenReturnBooking() {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResp.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingStatus.WAITING)));
    }

    @SneakyThrows
    @Test
    void getAllBookingsUser_whenInputValueValid_thenReturnListBooking() {
        List<Booking> list = List.of(booking);
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$[0].status").value(String.valueOf(list.get(0).getStatus())))
                .andExpect(jsonPath("$[0].booker.id").value(String.valueOf(list.get(0).getBookingUser().getId())));
    }

    @SneakyThrows
    @Test
    void getAllBookingsUser_whenInputValueNotValid_thenReturnTrows() {
        List<Booking> list = List.of(booking);
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "p")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllItemsUser_whenInputValueValid_thenReturnListBooking() {
        List<Booking> list = List.of(booking);
        when(bookingService.getUserItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings/owner", bookingUser.getId())
                        .header(USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "15"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$[0].booker.id").value(list.get(0).getBookingUser().getId()))
                .andExpect(jsonPath("$[0].item.name").value(list.get(0).getItem().getName()));
        verify(bookingService, times(1)).getUserItems(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllItemsUser_whenInputValueNotValid_thenReturnThrows() {
        List<Booking> list = List.of(booking);
        when(bookingService.getUserItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings/owner", bookingUser.getId())
                        .header(USER_ID, "q")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "15"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}