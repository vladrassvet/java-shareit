package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService service;

    private static final String USER_ID = "X-Sharer-User-Id";

    private User user;
    private User requestor;

    private ItemRequestDto itemRequestDto;

    private ItemRequest request;

    private ItemRequestResponse response;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("alex@mail.ru")
                .name("alex")
                .build();
        requestor = User.builder()
                .id(2L)
                .email("sema@mail.ru")
                .name("sema")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requestor(requestor.getId())
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        response = ItemRequestMapper.toItemRequestResponse(request, new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void addRequest_whenItemRequestDtoValid_thenReturnItemRequest() {
        when(service.addRequest(anyLong(), any(ItemRequestDto.class), any(LocalDateTime.class)))
                .thenReturn(request);

        mockMvc.perform(post("/requests", requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID, "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.requestor.email").value(request.getRequestor().getEmail()));
    }

    @SneakyThrows
    @Test
    void addRequest_whenItemRequestDtoNotValid_thenReturnIsBadRequest() {
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .requestor(requestor.getId())
                .description("")
                .build();
        mockMvc.perform(post("/requests", requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto1))
                        .header(USER_ID, "2"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllMyRequests_whenUserIdValid_thenReturnListItemRequestResponse() {
        List<ItemRequestResponse> responseList = List.of(response);
        when(service.getAllMyRequests(user.getId())).thenReturn(responseList);

        mockMvc.perform(get("/requests", user.getId())
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(responseList.get(0).getDescription()));
    }

    @SneakyThrows
    @Test
    void getListOfOtherUsersRequests_whenInputValueValid_thenReturnListItemRequestResponse() {
        List<ItemRequestResponse> responseList = List.of(response);
        when(service.getListOfOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(responseList);

        mockMvc.perform(get("/requests/all", user.getId())
                        .header(USER_ID, "1")
                        .param("from", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(responseList.get(0).getDescription()));
    }

    @SneakyThrows
    @Test
    void getListOfOtherUsersRequests_whenUserIdNotValid_thenReturnIsBadRequest() {
        List<ItemRequestResponse> responseList = List.of(response);
        when(service.getListOfOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(responseList);

        mockMvc.perform(get("/requests/all", user.getId())
                        .header(USER_ID, "q")
                        .param("from", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getItemRequest_whenUserIdValid_thenReturnItemRequestResponse() {
        when(service.getItemRequest(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", user.getId())
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }
}