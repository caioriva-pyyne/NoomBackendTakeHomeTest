package com.noom.interview.fullstack.sleep.validator;

import com.noom.interview.fullstack.sleep.validator.annotation.WithTimeGreaterOrLesserThan;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WithTimeGreaterOrLessThanValidator implements ConstraintValidator<WithTimeGreaterOrLesserThan, LocalDateTime> {
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public void initialize(WithTimeGreaterOrLesserThan constraintAnnotation) {
        this.startTime = LocalTime.parse(constraintAnnotation.greaterThan(), DateTimeFormatter.ISO_LOCAL_TIME);
        this.endTime = LocalTime.parse(constraintAnnotation.lesserThan(), DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalTime timeToCheck = value.toLocalTime();
        return timeToCheck.isAfter(startTime) || timeToCheck.isBefore(endTime);
    }
}
