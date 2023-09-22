package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.workFolder.utilite.CustomPageRequest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static ru.practicum.ewm.workFolder.validation.Validator.getNonNullObject;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto add(CategoryDto dto) {
        checkNameCategory(dto.getName());
        Category saved = categoryRepository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto dto) {
        Category entity = getNonNullObject(categoryRepository, catId, Category.class);
        if (entity.getName().equals(dto.getName())) {
            return categoryMapper.toDto(entity);
        } else if (!entity.getName().equals(dto.getName())) {
            checkNameCategory(dto.getName());
        }
        return categoryMapper.toDto(categoryMapper.updateEntity(dto, entity));
    }

    @Override
    @Transactional
    public void remove(Long catId) {
        checkCategoryEmpty(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        return categoryMapper.toDto(page.getContent());
    }

    @Override
    @Transactional
    public CategoryDto findById(Long catId) {
        Category entity = getNonNullObject(categoryRepository, catId, Category.class);
        return categoryMapper.toDto(entity);
    }

    private void checkCategoryEmpty(Long catId) {
        Event event = new Event().setCategory(new Category().setId(catId));
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("category.id", exact());
        if (eventRepository.exists(Example.of(event, matcher))) {
            throw new ConflictException("Категория не пуста.");
        }
    }

    private void checkNameCategory(String name) {
        if (categoryRepository.findCategoryByName(name) != null) {
            throw new ConflictException("Имя уже занято.");
        }
    }
}