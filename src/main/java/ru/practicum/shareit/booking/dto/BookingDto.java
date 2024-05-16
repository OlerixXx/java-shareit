package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.groups.Create;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @NotBlank(message = "Идентификатор не может быть пустым.", groups = Create.class)
    private Long item_id;
    @NotBlank(message = "Время начала не может быть пустым.", groups = Create.class)
    private Instant start;
    @NotBlank(message = "Время окончания не может быть пустым.", groups = Create.class)
    private Instant end;

}
