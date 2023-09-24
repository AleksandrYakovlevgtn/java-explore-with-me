package ru.practicum.ewm.category.model;

import org.mapstruct.Mapper;
import ru.practicum.ewm.workFolder.EntityMapper;
import ru.practicum.ewm.category.dto.CategoryDto;

@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends EntityMapper<Category, CategoryDto> {
}