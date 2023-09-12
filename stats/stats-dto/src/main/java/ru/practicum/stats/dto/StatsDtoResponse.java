package ru.practicum.stats.dto;

import lombok.Value;

@Value
public class StatsDtoResponse {
    String app;
    String url;
    String hits;
}