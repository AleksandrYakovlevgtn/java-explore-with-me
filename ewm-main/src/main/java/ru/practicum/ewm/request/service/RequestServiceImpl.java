package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.dto.RequestDtoParticipation;
import ru.practicum.ewm.request.model.RequestMapper;
import ru.practicum.ewm.enums.RequestState;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.ewm.workFolder.validation.Validator.checkId;
import static ru.practicum.ewm.workFolder.validation.Validator.getNonNullObject;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public RequestDtoParticipation add(Long userId, Long eventId) {
        checkId(userRepository, userId);
        Event event = getNonNullObject(eventRepository, eventId);
        checkNotInitiator(userId, event);
        checkNotRepeatedRequest(userId, eventId);
        checkPublished(event);
        checkParticipantLimit(event);
        checkNotReachedLimit(event);
        Request request = new Request()
                .setCreated(LocalDateTime.now())
                .setEvent(new Event().setId(eventId))
                .setRequester(new User().setId(userId))
                .setStatus(event.getRequestModeration() ? RequestState.PENDING : RequestState.CONFIRMED);
        request = requestRepository.save(request);
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return requestMapper.toDto(request);
    }

    @Override
    public RequestDtoParticipation cancelById(Long userId, Long requestId) {
        checkId(userRepository, userId);
        Request request = getNonNullObject(requestRepository, requestId);
        checkRequester(userId, request);
        request.setStatus(RequestState.CANCELED);
        request = requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<RequestDtoParticipation> findByUserId(Long userId) {
        checkId(userRepository, userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.toDto(requests);
    }

    private void checkRequester(Long userId, Request request) {
        if (!Objects.equals(userId, request.getRequester().getId())) {
            throw new ConflictException("Вы можете изменить только свой запрос");
        }
    }

    private void checkNotReachedLimit(Event event) {
        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConflictException("Достигнут лимит заявок на участие.");
        }
    }

    private void checkPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Вы не можете участвовать в неопубликованном мероприятии.");
        }
    }

    private void checkParticipantLimit(Event event) {
        if (event.getParticipantLimit() - event.getConfirmedRequests() < 0 && event.getParticipantLimit() - event.getConfirmedRequests() != 0) {
            throw new ConflictException("Достигнут лимит заявок на участие.");
        }
    }

    private void checkNotRepeatedRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Вы не можете добавить повторный запрос.");
        }
    }

    private void checkNotInitiator(Long userId, Event event) {
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не может добавить заявку на участие в своем мероприятии.");
        }
    }
}