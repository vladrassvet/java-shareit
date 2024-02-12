package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UserDto userDto1;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .email("alex@yandex.ru")
                .name("alex")
                .build();

        userDto1 = UserDto.builder()
                .id(2L)
                .email("alexander@yandex.ru")
                .name("alexander")
                .build();

        user = User.builder()
                .id(1L)
                .email("alex@yandex.ru")
                .name("alex")
                .build();
    }

    @SneakyThrows
    @Test
    void getAllUserTest() {
        List<UserDto> users = List.of(userDto, userDto1);

        when(userService.getAllUsers())
                .thenReturn(users.stream().map(UserMapper::dtoToUser).collect(Collectors.toList()));
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[1].name").value(userDto1.getName()));
    }

    @SneakyThrows
    @Test
    void createUser_whenInputUserValid_thenReturnSavesUser() {
        when(userService.createUser(UserMapper.dtoToUser(userDto)))
                .thenReturn(UserMapper.dtoToUser(userDto));

        mockMvc.perform(post("/users", userDto)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()));
        verify(userService).createUser(UserMapper.dtoToUser(userDto));
    }

    @SneakyThrows
    @Test
    void createUser_whenInputUserNotValid_thenReturnThrows() {
        UserDto userDto3 = UserDto.builder()
                .id(1L)
                .email(null)
                .name("alex")
                .build();
        mockMvc.perform(post("/users", userDto3)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto3)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(UserMapper.dtoToUser(userDto3));
    }

    @SneakyThrows
    @Test
    void updateUserById() {
        long userId = 1L;
        UserDto userDto3 = UserDto.builder()
                .id(1L)
                .email(null)
                .name("alex")
                .build();

        when(userService.updateUser(UserMapper.userDtoToUserByid(userId, userDto3)))
                .thenReturn(UserMapper.dtoToUser(userDto3));
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto3)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto3.getName()));
        verify(userService, times(1)).updateUser(UserMapper.userDtoToUserByid(userId, userDto3));
    }

    @SneakyThrows
    @Test
    void deleteUserByIdTest() {
        long id = 1L;
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(id);
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() throws Exception {
        UserDto userDto3 = UserMapper.userToDto(user);

        when(userService.getUserById(user.getId()))
                .thenReturn(user);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto3.getId()))
                .andExpect(jsonPath("$.name").value(userDto3.getName()))
                .andExpect(jsonPath("$.email").value(userDto3.getEmail()));
        verify(userService).getUserById(user.getId());
    }
}