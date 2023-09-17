package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.workFolder.utilite.CustomPageRequest;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.enums.AdminActionState;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.dto.RequestDtoParticipation;
import ru.practicum.ewm.request.model.RequestMapper;
import ru.practicum.ewm.enums.RequestState;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.StatsDtoResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.workFolder.validation.Validator.checkId;
import static ru.practicum.ewm.workFolder.validation.Validator.getNonNullObject;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Override
    public EventDtoFull add(Long userId, EventDtoNew dto) {
        checkId(userRepository, userId);
        checkId(categoryRepository, dto.getCategoryId());
        Event entity = eventMapper.toEntity(dto, userId);
        Event added = eventRepository.save(entity);
        return eventMapper.toDto(added);
    }

    @Override
    public EventDtoFull updateById(Long initiatorId, Long eventId, EventDtoUpdateUserRequest dto) {
        checkId(userRepository, initiatorId);
        if (Objects.nonNull(dto.getCategoryId())) {
            checkId(categoryRepository, dto.getCategoryId());
        }
        Event entity = getNonNullObject(eventRepository, eventId);
        checkInitiator(initiatorId, entity);
        checkNotPublished(entity);
        entity = eventMapper.updateEntity(dto, entity, categoryRepository);
        entity = eventRepository.save(entity);
        return eventMapper.toDto(entity);
    }

    @Override
    public List<RequestDtoParticipation> findRequestsByEventId(Long initiatorId, Long eventId) {
        checkId(userRepository, initiatorId);
        checkId(eventRepository, eventId);
        List<Request> entities = requestRepository.findAllByEventId(eventId);
        return requestMapper.toDto(entities);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestState(Long initiatorId, Long eventId, EventRequestStatusUpdateRequest dto) {
        checkId(userRepository, initiatorId);
        Event event = getNonNullObject(eventRepository, eventId);
        List<Request> requests = requestRepository.findByIdIn(dto.getRequestIds());
        if (!event.getRequestModeration() || event.getParticipantLimit() <= 0) {
            throw new ConflictException("Нет необходимости подтверждать запросы.");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Количество участников ограничено.");
        }
        List<RequestDtoParticipation> confirmed = new ArrayList<>();
        List<RequestDtoParticipation> rejected = new ArrayList<>();
        requests.forEach(
                request -> {
                    if (RequestState.PENDING != request.getStatus()) {
                        throw new ConflictException("Запрос должен иметь статус PENDING");
                    }
                    if (dto.getStatus() == RequestState.CONFIRMED && event.getParticipantLimit() - event.getConfirmedRequests() > 0) {
                        request.setStatus(RequestState.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmed.add(requestMapper.toDto(request));
                    } else {
                        request.setStatus(RequestState.REJECTED);
                        rejected.add(requestMapper.toDto(request));
                    }
                }
        );
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    public EventDtoFull adminUpdateById(Long eventId, EventDtoUpdateAdminRequest dto) {
        if (Objects.nonNull(dto.getCategoryId())) {
            checkId(categoryRepository, dto.getCategoryId());
        }
        Event entity = getNonNullObject(eventRepository, eventId);
        checkAdminActionState(dto, entity);
        checkEventDate(entity, dto);
        entity = eventMapper.updateEntity(dto, entity, categoryRepository);
        entity = eventRepository.save(entity);
        return eventMapper.toDto(entity);
    }

    @Override
    public List<EventDtoFull> adminFindAllByFilter(AdminSearchFilter filter, Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        BooleanBuilder booleanBuilder = makeBooleanBuilder(filter);
        Page<Event> page = eventRepository.findAll(booleanBuilder, pageable);
        return eventMapper.toDto(page.getContent());
    }

    @Override
    public List<EventDtoShort> publicFindAllByFilter(AdminSearchFilter filter, EventSort eventSort, Integer from, Integer size) {
        Sort sort = eventSort == EventSort.EVENT_DATE
                ? Sort.by("eventDate").ascending()
                : Sort.by("views").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        BooleanBuilder booleanBuilder = makeBooleanBuilder(filter);
        Page<Event> page = eventRepository.findAll(booleanBuilder, pageable);
        List<Event> entities = page.getContent();
        setViews(entities);
        return eventMapper.toShortDtos(page.getContent());
    }

    @Override
    public EventDtoFull publicFindById(Long eventId) {
        Event entity = getNonNullObject(eventRepository, eventId);
        if (entity.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("event с id=%s не найден.", eventId));
        }
        setViews(List.of(entity));
        return eventMapper.toDto(entity);
    }

    @Override
    public List<EventDtoShort> findAllByInitiatorId(Long initiatorId, Integer from, Integer size) {
        checkId(userRepository, initiatorId);
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<Event> page = eventRepository.findAllByInitiatorId(initiatorId, pageable);
        List<Event> entities = page.getContent();
        setViews(entities);
        return eventMapper.toShortDtos(entities);
    }

    @Override
    public EventDtoFull findById(Long initiatorId, Long eventId) {
        checkId(userRepository, initiatorId);
        Event entity = getNonNullObject(eventRepository, eventId);
        setViews(List.of(entity));
        return eventMapper.toDto(entity);
    }

    private void setViews(List<Event> events) {
        setViews(events, LocalDateTime.of(0, 1, 1, 0, 0, 0), LocalDateTime.now());
    }

    private void setViews(List<Event> events, LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(events) || events.isEmpty()) {
            return;
        }
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<StatsDtoResponse> dtos = null;
        try {
            ResponseEntity<Object> responseEntity = statsClient.search(start, end, uris, true);
            Object body = responseEntity.getBody();
            if (body != null) {
                TypeReference<List<StatsDtoResponse>> typeRef = new TypeReference<>() {
                };
                dtos = objectMapper.readValue(body.toString(), typeRef);
            }
        } catch (RestClientException | JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        List<StatsDtoResponse> finalDtos = dtos;
        events.forEach(event -> setViewsFromSources(event, finalDtos != null ? finalDtos : List.of()));
    }

    private void setViewsFromSources(Event event, List<StatsDtoResponse> sources) {
        (Objects.isNull(sources) ? new ArrayList<StatsDtoResponse>(0) : sources)
                .stream()
                .filter(Objects::nonNull)
                .filter(dto -> ("/events/" + event.getId()).equals(dto.getUri()))
                .findFirst()
                .ifPresentOrElse(
                        dto -> event.setViews(dto.getHits()),
                        () -> event.setViews(0L));
    }

    private void checkNotPublished(Event entity) {
        if (EventState.PUBLISHED.equals(entity.getState())) {
            throw new ConflictException("Событие не должно быть опубликовано.");
        }
    }

    private void checkInitiator(Long initiatorId, Event entity) {
        if (!Objects.equals(initiatorId, entity.getInitiator().getId())) {
            throw new ConflictException("Только создатель может вносить изменения.");
        }
    }

    private void checkAdminActionState(EventDtoUpdateAdminRequest dto, Event entity) {
        if (Objects.isNull(dto.getStateAction())) {
            return;
        }
        if (dto.getStateAction() == AdminActionState.PUBLISH_EVENT && entity.getState() != EventState.PENDING) {
            throw new ConflictException("Событие может быть опубликовано только в том случае, если оно находится в состоянии ожидания публикации.");
        }
        if (dto.getStateAction() == AdminActionState.REJECT_EVENT && entity.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Событие может быть отклонено только в том случае, если оно еще не было опубликовано.");
        }
    }

    private void checkEventDate(Event entity, EventDtoUpdateAdminRequest dto) {
        if (dto.getEventDate() != null) {
            if (entity.getPublishedOn() != null && dto.getEventDate().isBefore(entity.getPublishedOn().plusHours(1))
                    || dto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ConflictException("Дата начала изменяемого мероприятия не должна быть ранее " +
                        " чем через час с момента публикации.");
            }
        }
    }

    private BooleanBuilder makeBooleanBuilder(AdminSearchFilter filter) {
        java.util.function.Predicate<Object> isNullOrEmpty = obj ->
                Objects.isNull(obj) || (obj instanceof Collection && ((Collection<?>) obj).isEmpty());
        QEvent qEvent = QEvent.event;
        return new BooleanBuilder()
                .and(!isNullOrEmpty.test(filter.getUserIds()) ? qEvent.initiator.id.in(filter.getUserIds()) : null)
                .and(!isNullOrEmpty.test(filter.getStates()) ? qEvent.state.in(filter.getStates()) : null)
                .and(!isNullOrEmpty.test(filter.getCategoryIds()) ? qEvent.category.id.in(filter.getCategoryIds()) : null)
                .and(!isNullOrEmpty.test(filter.getRangeStart()) ? qEvent.eventDate.after(filter.getRangeStart()) : null)
                .and(!isNullOrEmpty.test(filter.getPaid()) ? qEvent.paid.eq(filter.getPaid()) : null)
                .and(!isNullOrEmpty.test(filter.getText())
                        ? (qEvent.annotation.likeIgnoreCase(filter.getText()).or(qEvent.description.likeIgnoreCase(filter.getText()))) : null)
                .and(!isNullOrEmpty.test(filter.getOnlyAvailable())
                        ? qEvent.participantLimit.eq(0).or(qEvent.confirmedRequests.lt(qEvent.participantLimit)) : null);
    }
}