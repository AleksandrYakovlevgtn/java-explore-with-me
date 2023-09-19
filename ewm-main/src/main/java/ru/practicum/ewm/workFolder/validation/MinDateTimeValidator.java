package ru.practicum.ewm.workFolder.validation;

import ru.practicum.ewm.exception.BadRequestException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class MinDateTimeValidator implements ConstraintValidator<MinDateTime, LocalDateTime> {

    private String defaultMessage;

    @Override
    public void initialize(MinDateTime constraintAnnotation) {
        this.defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(LocalDateTime testedDate, ConstraintValidatorContext constraintValidatorContext) {
        if (testedDate != null && !LocalDateTime.now().plusHours(2).isBefore(testedDate)) {
            throw new BadRequestException(defaultMessage);
        }
        return true;
    }
}