package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithoutUserDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
