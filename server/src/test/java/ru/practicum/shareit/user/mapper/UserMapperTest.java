package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "alex@yandex.ru", "alex");
    }

    @Test
    void userToDto_whenUserFound_thenReturnUserDto() {
        UserDto userDto = UserMapper.userToDto(user);
        assertEquals("alex@yandex.ru", userDto.getEmail(), "Некорректно отработал Mapper");
        assertEquals("alex", userDto.getName(), "Некорректно отработал Mapper");
    }

    @Test
    void dtoToUser_whenUserFound_thenReturnUser() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("petya@yandex.ru")
                .name("petya")
                .build();
        User actualUser = UserMapper.dtoToUser(userDto);
        assertEquals("petya@yandex.ru", actualUser.getEmail(), "Некорректно отработал Mapper");
        assertEquals("petya", actualUser.getName(), "Некорректно отработал Mapper");
    }

    @Test
    void userDtoToUserByid_whenUserFound_thenReturnUser() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("petya@yandex.ru")
                .name("petya")
                .build();
        User newUser = UserMapper.userDtoToUserByid(userDto.getId(), userDto);
        assertEquals("petya@yandex.ru", newUser.getEmail(), "Некорректно отработал Mapper");
        assertEquals("petya", newUser.getName(), "Некорректно отработал Mapper");
        assertEquals(2L, newUser.getId(), "Некорректно отработал Mapper");
    }
}