package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.items.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Generated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestResponse {

    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
