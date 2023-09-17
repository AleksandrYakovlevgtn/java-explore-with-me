package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.workFolder.utilite.CustomPageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.CompilationDtoUpdate;
import ru.practicum.ewm.compilation.model.*;
import ru.practicum.ewm.compilation.repository.CompilationRepository;

import java.util.List;

import static ru.practicum.ewm.workFolder.validation.Validator.checkId;
import static ru.practicum.ewm.workFolder.validation.Validator.getNonNullObject;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto add(CompilationDtoNew dto) {
        Compilation compilation = compilationMapper.toEntity(dto);
        compilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto update(Long compId, CompilationDtoUpdate dto) {
        Compilation entity = getNonNullObject(compilationRepository, compId);
        Compilation updated = compilationMapper.updateEntity(dto, entity);
        updated = compilationRepository.save(updated);
        return compilationMapper.toDto(updated);
    }

    @Override
    public void remove(Long compId) {
        checkId(compilationRepository, compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<Compilation> page = compilationRepository.findAllByPinned(pinned, pageable);
        return compilationMapper.toDto(page.getContent());
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation entity = getNonNullObject(compilationRepository, compId);
        return compilationMapper.toDto(entity);
    }
}