package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;
import ru.practicum.shareit.validator.EndAfterStart;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EndAfterStart(message = "Дата окончания должна быть позже даты начала", groups = {Create.class, Update.class})
public class BookingDto {
    private Long itemId;
    @NotNull(groups = Create.class)
    @FutureOrPresent(message = "Дата начала не может быть в прошлом", groups = {Create.class, Update.class})
    private LocalDateTime start;
    @NotNull(groups = Create.class)
    @FutureOrPresent(message = "Дата окончания не может быть в прошлом", groups = {Create.class, Update.class})
    private LocalDateTime end;
}
