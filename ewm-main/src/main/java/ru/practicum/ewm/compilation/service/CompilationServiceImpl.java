package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.workFolder.utilite.CustomPageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.CompilationDtoUpdate;
import ru.practicum.ewm.compilation.model.*;
import ru.practicum.ewm.compilation.repository.CompilationRepository;

import java.util.HashSet;
import java.util.List;

import static ru.practicum.ewm.workFolder.validation.Validator.checkId;
import static ru.practicum.ewm.workFolder.validation.Validator.getNonNullObject;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto add(CompilationDtoNew dto) {
        Compilation compilation = new Compilation();
        if (dto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEventIds() == null) {
            compilation.setEvents(new HashSet<>());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(dto.getEventIds()));
        }
        compilation.setTitle(dto.getTitle());
        compilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, CompilationDtoUpdate dto) {
        Compilation entity = getNonNullObject(compilationRepository, compId);
        if (dto.getPinned() != null) {
            entity.setPinned(dto.getPinned());
        }
        if (dto.getEventIds() != null) {
            entity.setEvents(eventRepository.findByIdIn(dto.getEventIds()));
        }
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        return compilationMapper.toDto(entity);
    }

    @Override
    @Transactional
    public void remove(Long compId) {
        checkId(compilationRepository, compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<Compilation> page = compilationRepository.findAllByPinned(pinned, pageable);
        return compilationMapper.toDto(page.getContent());
    }

    @Override
    @Transactional
    public CompilationDto findById(Long compId) {
        Compilation entity = getNonNullObject(compilationRepository, compId);
        return compilationMapper.toDto(entity);
    }
}