package ru.practicum.ewm.enums;

public enum EventSort {
    EVENT_DATE,
    VIEWS;

    public static EventSort by(String stateName) {
        try {
            return EventSort.valueOf(stateName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + stateName, e);
        }
    }
}
