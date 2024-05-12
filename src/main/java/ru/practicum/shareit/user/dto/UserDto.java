package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Имя не может быть пустым.", groups = Create.class)
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой.", groups = Create.class)
    @Email(message = "Электронная почта не соответсвует формату.", groups = {Create.class, Update.class})
    private String email;
}
