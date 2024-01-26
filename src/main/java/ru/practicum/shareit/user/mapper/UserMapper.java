package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static User userDtoToUserByid(long id, UserDto userDto) {
        return User.builder()
                .id(id)
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
