package ru.practicum.ewm.subscription.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.workFolder.utilite.CustomPageRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.dto.EventDtoShort;
import ru.practicum.ewm.event.model.EventMapper;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoRequest;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoResponse;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoUpdate;
import ru.practicum.ewm.subscription.model.*;
import ru.practicum.ewm.subscription.filter.SubscriptionFilter;
import ru.practicum.ewm.subscription.filter.SubscriptionFilterHelper;
import ru.practicum.ewm.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.dto.UserDtoShort;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.workFolder.validation.Validator.checkId;
import static ru.practicum.ewm.workFolder.validation.Validator.getNonNullObject;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final String ENTITY_SIMPLE_NAME = Subscription.class.getSimpleName();
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final EventMapper eventMapper;

    @Override
    public SubscriptionDtoResponse add(Long userId, SubscriptionDtoRequest dto) {
        checkId(userRepository, userId);
        checkSubscriptionExists(userId);
        checkFavoriteObservable(dto.getFavorites());
        Subscription subscription = subscriptionMapper.toEntity(dto);
        subscription.setOwner(new User().setId(userId));
        subscriptionRepository.save(subscription);
        return subscriptionMapper.toDtoResponse(subscription);
    }

    @Override
    public SubscriptionDtoResponse update(Long userId, Long sbrId, SubscriptionDtoUpdate dto) {
        checkId(userRepository, userId);
        Subscription subscription = getNonNullObject(subscriptionRepository, sbrId);
        checkOwner(userId, subscription);
        checkFavoriteObservable(dto.getFavorites());
        subscription = subscriptionMapper.updateEntity(dto, subscription);
        subscriptionRepository.save(subscription);
        return subscriptionMapper.toDtoResponse(subscription);
    }

    @Override
    public void remove(Long userId, Long sbrId) {
        Subscription subscription = getNonNullObject(subscriptionRepository, sbrId);
        checkOwner(userId, subscription);
        subscriptionRepository.deleteById(sbrId);
    }

    @Override
    public SubscriptionDtoResponse find(Long userId) {
        Subscription subscription = subscriptionRepository.findByOwnerId(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("%s с ownerId=%s не найден.", ENTITY_SIMPLE_NAME, userId)));
        return subscriptionMapper.toDtoResponse(subscription);
    }

    @Override
    public List<EventDtoShort> findFavoriteEvents(Long userId, SubscriptionFilter filter, EventSort eventSort, Integer from, Integer size) {
        Sort sort = eventSort == EventSort.EVENT_DATE
                ? Sort.by("eventDate").ascending()
                : Sort.by("views").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);

        List<Long> favoriteIds = findFavoriteIds(userId);
        Optional<BooleanBuilder> oBuilder = SubscriptionFilterHelper.makeBooleanBuilder(favoriteIds, filter);
        if (oBuilder.isEmpty()) {
            return List.of();
        }

        BooleanBuilder booleanBuilder = oBuilder.get();
        Page<Event> page = eventRepository.findAll(booleanBuilder, pageable);
        return eventMapper.toShortDtos(page.getContent());
    }

    private void checkOwner(Long userId, Subscription sbr) {
        if (!Objects.equals(sbr.getOwner().getId(), userId)) {
            throw new ConflictException(String.format("Пользователь с id=%s не является владельцем подписки.", userId));
        }
    }

    private void checkFavoriteObservable(Set<Long> favorites) {
        if (userRepository.existsByIdInAndObservable(favorites, false)) {
            throw new ConflictException(("Избранное должно быть 'видимым'."));
        }
    }

    private void checkSubscriptionExists(Long userId) {
        if (subscriptionRepository.existsByOwnerId(userId)) {
            throw new ConflictException(String.format("Подписка для пользователя с id=%s уже существует.", userId));
        }
    }

    private List<Long> findFavoriteIds(Long userId) {
        SubscriptionDtoResponse dto = find(userId);
        return dto.getFavorites().stream()
                .map(UserDtoShort::getId).collect(Collectors.toList());
    }
}