package ru.practicum.ewm.category;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "add")
    public ResponseEntity<CategoryDto> add(@Validated @RequestBody CategoryDto dto) {
        CategoryDto body = categoryService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/{catId}")
    @Operation(summary = "update")
    public ResponseEntity<CategoryDto> update(@Validated @RequestBody CategoryDto dto,
                                              @PathVariable Long catId) {
        CategoryDto body = categoryService.update(catId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/{catId}")
    @Operation(summary = "remove")
    public ResponseEntity<CategoryDto> remove(@PathVariable Long catId) {
        categoryService.remove(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}