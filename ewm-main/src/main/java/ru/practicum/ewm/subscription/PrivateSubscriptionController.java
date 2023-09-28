package ru.practicum.ewm.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDtoShort;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoRequest;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoResponse;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoUpdate;
import ru.practicum.ewm.subscription.filter.SubscriptionFilter;
import ru.practicum.ewm.subscription.service.SubscriptionService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Private Subscription")
@RequestMapping("/users/{userId}/subscriptions")
public class PrivateSubscriptionController {

    private final SubscriptionService subscriptionService;


    @PostMapping
    @Operation(summary = "add")
    public ResponseEntity<SubscriptionDtoResponse> add(
            @PathVariable Long userId,
            @Valid @RequestBody SubscriptionDtoRequest dto) {
        SubscriptionDtoResponse body = subscriptionService.add(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/{subscriptionId}")
    @Operation(summary = "update")
    public ResponseEntity<SubscriptionDtoResponse> update(
            @PathVariable("userId") Long userId,
            @PathVariable("subscriptionId") Long sbrId,
            @Valid @RequestBody SubscriptionDtoUpdate dto) {
        SubscriptionDtoResponse body = subscriptionService.update(userId, sbrId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/{sbrId}")
    @Operation(summary = "remove")
    public ResponseEntity<Object> remove(
            @PathVariable("userId") Long userId,
            @PathVariable("sbrId") Long sbrId) {
        subscriptionService.remove(userId, sbrId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "find")
    public ResponseEntity<SubscriptionDtoResponse> find(
            @PathVariable("userId") Long userId) {
        SubscriptionDtoResponse body = subscriptionService.find(userId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/events")
    @Operation(summary = "findFavoriteEvents")
    public ResponseEntity<List<EventDtoShort>> findFavoriteEvents(
            SubscriptionFilter filter,
            @PathVariable("userId") Long userId,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        EventSort eventSort = sort != null ? EventSort.by(sort) : EventSort.EVENT_DATE;
        List<EventDtoShort> body = subscriptionService.findFavoriteEvents(userId, filter, eventSort, from, size);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}