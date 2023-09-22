package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
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
    @Transactional
    public UserDto add(UserDto dto) {
        checkNameUser(dto.getName());
        User saved = userRepository.save(userMapper.toEntity(dto));
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<UserDto> find(List<Long> userIds, Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        CustomPageRequest pageable = CustomPageRequest.by(from, size, sort);
        Page<User> page = Objects.nonNull(userIds) && !userIds.isEmpty()
                ? userRepository.findByIdIn(userIds, pageable)
                : userRepository.findAll(pageable);
        return userMapper.toDto(page.getContent());
    }

    @Override
    @Transactional
    public void remove(Long userId) {
        checkId(userRepository, userId, User.class);
        userRepository.deleteById(userId);
    }

    private void checkNameUser(String name) {
        if (userRepository.findUserByName(name) != null) {
            throw new ConflictException("Имя уже занято.");
        }
    }
}