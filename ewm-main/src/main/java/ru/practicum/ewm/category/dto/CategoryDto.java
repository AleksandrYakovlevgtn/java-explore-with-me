package ru.practicum.ewm.category.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class CategoryDto {
    Long id;
    @NotBlank
    String name;
}