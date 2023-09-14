package ru.practicum.stats.server.model;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.StatsDtoRequest;

@Component
public class StatsMapper {
    public Stats toEntity(StatsDtoRequest dto) {
        return new Stats()
                .setId(dto.getId())
                .setApp(dto.getApp())
                .setUri(dto.getUri())
                .setIp(dto.getIp())
                .setTimestamp(dto.getTimestamp());
    }
}