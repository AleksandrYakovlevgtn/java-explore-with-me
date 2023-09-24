package ru.practicum.ewm.workFolder;

import org.mapstruct.*;

import java.util.List;

public interface EntityMapper<E, D> {
    E toEntity(D dto);

    D toDto(E entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    E updateEntity(D dto, @MappingTarget E targetEntity);

    List<D> toDto(List<E> entities);
}