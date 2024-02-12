package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserDto {

    private long id;
    private String email;
    private String name;
}
