package ru.practicum.ewm.event.dto;

import lombok.Value;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserDtoShort;

import java.time.LocalDateTime;

@Value
public class EventDtoShort {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String description;
    LocalDateTime eventDate;
    UserDtoShort initiator;
    Boolean paid;
    String title;
    Long views;
}