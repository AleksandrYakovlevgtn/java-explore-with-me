package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(CategoryDto dto);

    CategoryDto update(Long catId, CategoryDto dto);

    void remove(Long catId);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long catId);
}