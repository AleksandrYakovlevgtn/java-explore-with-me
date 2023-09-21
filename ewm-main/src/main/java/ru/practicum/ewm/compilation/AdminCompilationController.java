package ru.practicum.ewm.compilation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoNew;
import ru.practicum.ewm.compilation.dto.CompilationDtoUpdate;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @Operation(summary = "add")
    public ResponseEntity<CompilationDto> add(@Valid @RequestBody CompilationDtoNew dto) {
        CompilationDto body = compilationService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/{compId}")
    @Operation(summary = "update")
    public ResponseEntity<CompilationDto> update(@Validated @RequestBody CompilationDtoUpdate dto,
                                                 @PathVariable Long compId) {
        CompilationDto body = compilationService.update(compId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/{compId}")
    @Operation(summary = "remove")
    public ResponseEntity<Object> remove(@PathVariable("compId") Long compId) {
        compilationService.remove(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}