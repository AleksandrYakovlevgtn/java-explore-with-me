package ru.practicum.ewm.category.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Value
public class CategoryDto {
    Long id;
    @Pattern(regexp = ".*\\S.*")
    @Size(max = 50)
    @NotBlank
    String name;
}