package ru.practicum.ewm.exception;

import javax.validation.ConstraintDeclarationException;

public class ConflictException extends ConstraintDeclarationException {
    public final String reason;

    public ConflictException(String reason, String message) {
        super(message);
        this.reason = reason;
    }

    public ConflictException(String message) {
        this("Для запрошенной операции условия не выполнены.", message);
    }

    public String getReason() {
        return reason;
    }
}