package ru.practicum.ewm.request.model;

import org.mapstruct.*;
import ru.practicum.ewm.workFolder.EntityMapper;
import ru.practicum.ewm.request.dto.RequestDtoParticipation;

@Mapper(componentModel = "spring")
public interface RequestMapper extends EntityMapper<Request, RequestDtoParticipation> {
    @Override
    @Mapping(target = "eventId", source = "entity.event.id")
    @Mapping(target = "requesterId", source = "entity.requester.id")
    RequestDtoParticipation toDto(Request entity);

    @Override
    @Mapping(target = "event.id", source = "dto.eventId")
    @Mapping(target = "requester.id", source = "dto.requesterId")
    Request toEntity(RequestDtoParticipation dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event.id", source = "dto.eventId")
    @Mapping(target = "requester.id", source = "dto.requesterId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Request updateEntity(RequestDtoParticipation dto, @MappingTarget Request targetEntity);
}