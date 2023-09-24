package ru.practicum.ewm.exception;

import javax.validation.ConstraintDeclarationException;

public class BadRequestException extends ConstraintDeclarationException {
    public final String reason;

    public BadRequestException(String reason, String message) {
        super(message);
        this.reason = reason;
    }

    public BadRequestException(String message) {
        this("Для запрошенной операции условия не выполнены.", message);
    }

    public String getReason() {
        return reason;
    }
}