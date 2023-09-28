package ru.practicum.ewm.subscription.dto;

import lombok.Value;
import ru.practicum.ewm.user.dto.UserDtoShort;

import java.util.List;

@Value
public class SubscriptionDtoResponse {

    Long id;

    List<UserDtoShort> favorites;
}