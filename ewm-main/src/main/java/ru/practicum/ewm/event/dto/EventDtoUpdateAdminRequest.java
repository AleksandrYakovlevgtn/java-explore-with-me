package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import ru.practicum.ewm.enums.AdminActionState;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
public class EventDtoUpdateAdminRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    @JsonProperty("category")
    Long categoryId;
    @Size(min = 20, max = 7000)
    String description;
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    AdminActionState stateAction;
    @Size(min = 3, max = 120)
    String title;
}