package ru.practicum.stats.server.stats;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.StatsDtoRequest;
import ru.practicum.stats.server.stats.model.Stats;

@Component
public class StatsMapper {
    public Stats toEntity(StatsDtoRequest dto) {
        return new Stats()
                .setId(dto.getId())
                .setApp(dto.getApp())
                .setUri(dto.getUri())
                .setIp(dto.getIp())
                .setTimestamp(dto.getTimeStamp());
    }
}