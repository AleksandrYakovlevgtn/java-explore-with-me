package ru.practicum.stats.server;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.StatsDtoRequest;
import ru.practicum.stats.dto.StatsDtoResponse;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;
    @Value("${app.format.date-time}")
    private String format;

    @PostMapping("/hit")
    public void add(@Valid @RequestBody StatsDtoRequest statsDtoRequest) {
        statsService.add(statsDtoRequest);
    }

    @GetMapping("/stats")
    public List<StatsDtoResponse> get(@RequestParam("start") String start,
                                      @RequestParam("end") String end,
                                      @RequestParam(value = "uris", required = false) List<String> uris,
                                      @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return statsService.search(toLocalDateTime(start), toLocalDateTime(end), uris, unique);
    }

    private LocalDateTime toLocalDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }
}