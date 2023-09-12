package ru.practicum.ewm.events;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {
    private final StatsClient statsClient;

    @GetMapping("/{id}")
    @Operation(summary = "findById")
    public ResponseEntity<Object> findById(@PathVariable("id") Long eventId,
                                           HttpServletRequest servletRequest) {
        log.info("findById");
        statsClient.add(servletRequest);
        return null;
    }

    @GetMapping
    @Operation(summary = "findAll")
    public ResponseEntity<Object> findAll(HttpServletRequest servletRequest) {
        log.info("findAll");
        statsClient.add(servletRequest);
        return null;
    }

    @GetMapping("/stats")
    @Operation(summary = "findStats")
    public ResponseEntity<Object> findStats() {
        log.info("findStats");
        return statsClient.search(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(1),
                List.of("/events", "/events/1", "/events/2"),
                true);
    }
}