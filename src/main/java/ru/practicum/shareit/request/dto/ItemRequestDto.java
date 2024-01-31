package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class ItemRequestDto {
    @NotBlank
    private String description;
    @NotNull
    private long requestor;
}
