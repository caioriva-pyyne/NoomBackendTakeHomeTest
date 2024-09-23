package com.noom.interview.fullstack.sleep.validator.annotation;

import com.noom.interview.fullstack.sleep.validator.WithTimeGreaterOrLessThanValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WithTimeGreaterOrLessThanValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithTimeGreaterOrLesserThan {
    String message() default "must be within the specified time range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String greaterThan();

    String lesserThan();
}
