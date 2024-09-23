package com.noom.interview.fullstack.sleep.validator;

import com.noom.interview.fullstack.sleep.validator.annotation.NotAFutureDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class NotAFutureDateTimeValidator implements ConstraintValidator<NotAFutureDateTime, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.isBefore(LocalDateTime.now());
    }
}
