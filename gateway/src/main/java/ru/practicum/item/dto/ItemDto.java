package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @NotBlank(message = "Имя не может быть пустым.", groups = Create.class)
    private String name;
    @NotBlank(message = "Описание не может быть пустым.", groups = Create.class)
    private String description;
    @NotNull(message = "Поле доступа не может быть пустым.", groups = Create.class)
    private Boolean available;
    @Null(groups = {})
    private Long requestId;
}