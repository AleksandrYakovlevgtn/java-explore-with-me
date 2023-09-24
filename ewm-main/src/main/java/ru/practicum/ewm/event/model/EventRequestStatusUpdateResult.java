package ru.practicum.ewm.event.model;

import lombok.Value;
import ru.practicum.ewm.request.dto.RequestDtoParticipation;

import java.util.List;

@Value
public class EventRequestStatusUpdateResult {
    List<RequestDtoParticipation> confirmedRequests;
    List<RequestDtoParticipation> rejectedRequests;
}