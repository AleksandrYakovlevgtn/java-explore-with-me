package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Value
public class CompilationDtoNew {
    @JsonProperty("events")
    Set<Long> eventIds;
    Boolean pinned;
    @NotBlank
    String title;
}