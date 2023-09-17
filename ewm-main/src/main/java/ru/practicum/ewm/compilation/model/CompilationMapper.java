package ru.practicum.ewm.compilation.model;

import org.mapstruct.*;
import ru.practicum.ewm.workFolder.EntityMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.CompilationDtoUpdate;
import ru.practicum.ewm.event.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface CompilationMapper extends EntityMapper<Compilation, CompilationDto> {
    @Mapping(target = "events", source = "dto.eventIds", qualifiedByName = "idsToEvents")
    Compilation toEntity(CompilationDtoNew dto);

    @Named("idsToEvents")
    default Set<Event> getEvents(Set<Long> eventIds) {
        return eventIds.stream().map(id -> new Event().setId(id)).collect(Collectors.toSet());
    }

    @Mapping(target = "events", source = "dto.eventIds", qualifiedByName = "idsToEvents")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Compilation updateEntity(CompilationDtoUpdate dto, @MappingTarget Compilation targetEntity);
}