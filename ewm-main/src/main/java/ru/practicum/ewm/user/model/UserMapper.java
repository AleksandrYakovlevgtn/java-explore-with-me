package ru.practicum.ewm.user.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.workFolder.EntityMapper;
import ru.practicum.ewm.user.dto.UserDto;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper extends EntityMapper<User, UserDto> {
    @Override
    @Mapping(target = "observable", source = "observable", defaultValue = "true")
    User toEntity(UserDto dto);
}