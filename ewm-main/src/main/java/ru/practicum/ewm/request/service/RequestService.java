package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDtoParticipation;

import java.util.List;

public interface RequestService {
    RequestDtoParticipation add(Long userId, Long eventId);

    RequestDtoParticipation cancelById(Long userId, Long requestId);

    List<RequestDtoParticipation> findByUserId(Long userId);
}