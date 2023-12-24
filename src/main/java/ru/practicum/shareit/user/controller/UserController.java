package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUser() {
        return userService.getAllUsers().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(UserMapper.dtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUserById(@PathVariable long id, @RequestBody UserDto userDto) {
        User user = userService.updateUser(UserMapper.userToDtoDyId(id, userDto));
        return UserMapper.userToDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        return UserMapper.userToDto(user);
    }
}
