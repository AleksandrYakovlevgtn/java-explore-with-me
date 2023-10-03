package ru.practicum.ewm.subscription.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Value
public class SubscriptionDtoRequest {

    Long id;

    @NotNull
    @NotEmpty
    Set<Long> favorites;
}