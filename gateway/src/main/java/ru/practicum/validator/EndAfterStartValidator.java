package ru.practicum.validator;


import ru.practicum.booking.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto == null) {
            return true;
        }

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null) {
            return true;
        }

        boolean isValid = end.isAfter(start);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата окончания должна быть позже даты начала и не равна ей")
                    .addPropertyNode("end")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
