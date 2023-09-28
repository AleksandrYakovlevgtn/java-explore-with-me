package ru.practicum.ewm.subscription.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Value
public class SubscriptionDtoUpdate {

    Long id;

    @NotNull
    Set<Long> favorites;
}