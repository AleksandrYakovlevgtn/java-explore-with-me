package ru.practicum.ewm.exception;

public class NotFoundException extends AbstractBaseException {
    public NotFoundException(String reason, String message) {
        super(reason, message);
    }

    public NotFoundException(String message) {
        this("Oбъект не был найден.", message);
    }
}