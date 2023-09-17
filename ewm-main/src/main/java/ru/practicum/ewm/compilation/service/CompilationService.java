package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.CompilationDtoUpdate;

import java.util.List;

public interface CompilationService {
    CompilationDto add(CompilationDtoNew dto);

    CompilationDto update(Long compId, CompilationDtoUpdate dto);

    void remove(Long compId);

    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);
}