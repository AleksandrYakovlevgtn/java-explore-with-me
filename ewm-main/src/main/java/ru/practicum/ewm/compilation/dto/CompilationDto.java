package ru.practicum.ewm.compilation.dto;

import lombok.Value;
import ru.practicum.ewm.event.dto.EventDtoShort;

import javax.validation.constraints.Size;
import java.util.Set;

@Value
public class CompilationDto {
    Long id;
    Set<EventDtoShort> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}