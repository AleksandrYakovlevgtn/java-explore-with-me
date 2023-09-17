package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.workFolder.utilite.CustomPageRequest;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

import static ru.practicum.ewm.workFolder.validation.Validator.checkId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto dto) {
        User saved = userRepository.save(userMapper.toEntity(dto));
        return userMapper.toDto(saved);
    }

    @Override
    public List<UserDto> find(List<Long> userIds, Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<User> page = Objects.nonNull(userIds) && !userIds.isEmpty()
                ? userRepository.findByIdIn(userIds, pageable)
                : userRepository.findAll(pageable);
      return userMapper.toDto(page.getContent());
    }

    @Override
    public void remove(Long userId) {
        checkId(userRepository, userId, User.class);
        userRepository.deleteById(userId);
    }
}