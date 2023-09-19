package ru.practicum.ewm.event;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import ru.practicum.ewm.event.model.AdminSearchFilter;
import ru.practicum.ewm.event.dto.EventDtoFull;
import ru.practicum.ewm.event.dto.EventDtoShort;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @Value("${app.format.date-time}")
    private String format;

    @GetMapping
    @Operation(summary = "publicFindAllByFilter")
    public ResponseEntity<List<EventDtoShort>> publicFindAllByFilter(
            HttpServletRequest servletRequest,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categoryIds,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) String sort,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        sendToStats(servletRequest);

        AdminSearchFilter filter = new AdminSearchFilter(
                null,
                null,
                categoryIds,
                rangeStart != null ? toLocalDateTime(rangeStart) : LocalDateTime.now(),
                toLocalDateTime(rangeEnd),
                text,
                paid,
                onlyAvailable
        );
        EventSort eventSort = sort != null ? EventSort.by(sort) : EventSort.EVENT_DATE;
        List<EventDtoShort> body = eventService.publicFindAllByFilter(filter, eventSort, from, size);
        if (body.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(body);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "publicFindById")
    public ResponseEntity<Object> publicFindById(
            HttpServletRequest servletRequest,
            @PathVariable("id") Long eventId) {

        sendToStats(servletRequest);

        EventDtoFull body = eventService.publicFindById(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    private LocalDateTime toLocalDateTime(String value) {
        return value != null ? LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format)) : null;
    }

    private void sendToStats(HttpServletRequest servletRequest) {
        try {
            statsClient.add(servletRequest);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }
}