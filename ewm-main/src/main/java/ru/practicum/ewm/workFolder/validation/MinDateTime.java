package ru.practicum.ewm.workFolder.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinDateTimeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinDateTime {
    String message() default "Дата не должна быть раньше чем за 2 часа от начала.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}