package ru.practicum.ewm.subscription.service;

import ru.practicum.ewm.event.dto.EventDtoShort;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoRequest;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoResponse;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoUpdate;
import ru.practicum.ewm.subscription.filter.SubscriptionFilter;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDtoResponse add(Long userId, SubscriptionDtoRequest dto);

    SubscriptionDtoResponse update(Long userId, Long subscriptionId, SubscriptionDtoUpdate dto);

    void remove(Long userId, Long sbrId);

    SubscriptionDtoResponse find(Long userId);

    List<EventDtoShort> findFavoriteEvents(Long userId, SubscriptionFilter filter, EventSort eventSort, Integer from, Integer size);
}