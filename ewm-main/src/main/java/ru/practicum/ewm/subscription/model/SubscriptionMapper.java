package ru.practicum.ewm.subscription.model;

import org.mapstruct.*;
import ru.practicum.ewm.workFolder.EntityMapper;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoRequest;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoResponse;
import ru.practicum.ewm.subscription.dto.SubscriptionDtoUpdate;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.model.UserMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface SubscriptionMapper extends EntityMapper<Subscription, SubscriptionDtoRequest> {

    @Override
    @Mapping(target = "favorites", source = "favorites", qualifiedByName = "idsToUsers")
    Subscription toEntity(SubscriptionDtoRequest dto);

    @Override
    @Mapping(target = "favorites", source = "favorites", qualifiedByName = "usersToIds")
    SubscriptionDtoRequest toDto(Subscription entity);

    @Override
    @Mapping(target = "favorites", source = "favorites", qualifiedByName = "idsToUsers")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Subscription updateEntity(SubscriptionDtoRequest dto, @MappingTarget Subscription targetEntity);

    @Mapping(target = "favorites", source = "favorites", qualifiedByName = "idsToUsers")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Subscription updateEntity(SubscriptionDtoUpdate dto, @MappingTarget Subscription targetEntity);

    @Mapping(target = "favorites", source = "favorites")
    SubscriptionDtoResponse toDtoResponse(Subscription entity);

    @Named("usersToIds")
    default Set<Long> getIds(Set<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toSet());
    }

    @Named("idsToUsers")
    default Set<User> getUsers(Set<Long> getIds) {
        return getIds.stream().map(id -> new User().setId(id)).collect(Collectors.toSet());
    }
}