package ru.practicum.ewm.workFolder.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.stats.client.StatsClient;

@Configuration
@RequiredArgsConstructor
public class StatsClientConfiguration {
    @Value("${app.stats-server-url}")
    private String serverUrl;
    @Value("${app.name}")
    private String app;
    @Value("${app.format.date-time}")
    private String format;

    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    public StatsClient makeStatsClient() {
        return new StatsClient(
                serverUrl,
                app,
                format,
                restTemplateBuilder
        );
    }
}