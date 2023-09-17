package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto dto);

    List<UserDto> find(List<Long> userIds, Integer from, Integer size);

    void remove(Long userId);
}