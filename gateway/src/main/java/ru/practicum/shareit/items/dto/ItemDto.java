package ru.practicum.shareit.items.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class ItemDto {

    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @Nullable
    private Long requestId;
}
