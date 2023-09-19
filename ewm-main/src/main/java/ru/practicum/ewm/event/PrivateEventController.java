package ru.practicum.ewm.event;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDtoFull;
import ru.practicum.ewm.event.dto.EventDtoNew;
import ru.practicum.ewm.event.dto.EventDtoShort;
import ru.practicum.ewm.event.dto.EventDtoUpdateUserRequest;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDtoParticipation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "add")
    public ResponseEntity<EventDtoFull> add(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid EventDtoNew dto) {
        EventDtoFull body = eventService.add(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    @Operation(summary = "findAllByInitiatorId")
    public ResponseEntity<List<EventDtoShort>> findAllByInitiatorId(
            @PathVariable("userId") Long initiatorId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        List<EventDtoShort> body = eventService.findAllByInitiatorId(initiatorId, from, size);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "findById")
    public ResponseEntity<EventDtoFull> findById(
            @PathVariable("userId") Long initiatorId,
            @PathVariable("eventId") Long eventId) {
        EventDtoFull body = eventService.findById(initiatorId, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "updateById")
    public ResponseEntity<EventDtoFull> updateById(
            @PathVariable("userId") Long initiatorId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Validated EventDtoUpdateUserRequest dto) {
        EventDtoFull body = eventService.updateById(initiatorId, eventId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/{eventId}/requests")
    @Operation(summary = "findRequestsByEventId")
    public ResponseEntity<List<RequestDtoParticipation>> findRequestsByEventId(
            @PathVariable("userId") Long initiatorId,
            @PathVariable("eventId") Long eventId) {
        List<RequestDtoParticipation> body = eventService.findRequestsByEventId(initiatorId, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PatchMapping("/{eventId}/requests")
    @Operation(summary = "updateRequestState")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestState(
            @PathVariable("userId") Long initiatorId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestStatusUpdateRequest dto) {
        EventRequestStatusUpdateResult body = eventService.updateRequestState(initiatorId, eventId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}