package ru.practicum.ewm.user.dto;

import lombok.Value;

import javax.validation.constraints.*;

@Value
public class UserDto {
    Long id;
    @Pattern(regexp = ".*\\S.*")
    @Size(min = 2, max = 250)
    @NotBlank
    String name;
    @Pattern(regexp = ".*\\S.*")
    @Size(min = 6, max = 254)
    @NotNull
    @Email
    String email;
    Boolean observable;
}