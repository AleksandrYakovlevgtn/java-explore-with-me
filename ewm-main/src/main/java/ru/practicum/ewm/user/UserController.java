package ru.practicum.ewm.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "add")
    public ResponseEntity<UserDto> add(@Validated @RequestBody UserDto dto) {
        UserDto body = userService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    @Operation(summary = "find")
    public ResponseEntity<List<UserDto>> find(
            @RequestParam(value = "ids", required = false) List<Long> userIds,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<UserDto> body = userService.find(userIds, from, size);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "remove")
    public ResponseEntity<Object> remove(@PathVariable("userId") Long userId) {
        userService.remove(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}