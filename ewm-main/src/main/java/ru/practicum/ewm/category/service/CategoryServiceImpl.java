package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
    public CategoryDto add(CategoryDto dto) {
        Category saved = categoryRepository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long catId, CategoryDto dto) {
        Category entity = getNonNullObject(categoryRepository, catId, Category.class);
        Category updated = categoryMapper.updateEntity(dto, entity);
        updated = categoryRepository.save(updated);
        return categoryMapper.toDto(updated);
    }

    @Override
    public void remove(Long catId) {
        checkCategoryEmpty(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        return categoryMapper.toDto(page.getContent());
    }

    @Override
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
}