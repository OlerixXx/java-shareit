package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    @NotBlank(message = "Имя не может быть пустым.", groups = Create.class)
    private String name;
    @NotBlank(message = "Описание не может быть пустым.", groups = Create.class)
    private String description;
    @NotNull(message = "Поле доступа не может быть пустым.", groups = Create.class)
    private Boolean available;
    private Long request;
}
