package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Set;

@Value
public class CompilationDtoUpdate {
    @JsonProperty("events")
    Set<Long> eventIds;
    Boolean pinned;
    String title;
}