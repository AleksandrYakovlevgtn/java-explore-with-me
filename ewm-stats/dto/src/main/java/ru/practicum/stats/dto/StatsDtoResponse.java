package ru.practicum.stats.dto;

import lombok.Value;

@Value
public class StatsDtoResponse {
    String app;
    String uri;
    Long hits;
}