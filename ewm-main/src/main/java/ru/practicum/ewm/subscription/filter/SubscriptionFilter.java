package ru.practicum.ewm.subscription.filter;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class SubscriptionFilter {
    List<Long> categoryIds;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean paid;
    Boolean onlyAvailable;
}