package io.github.aieducationbackend.controller;

import io.github.aieducationbackend.dto.UserDto;
import io.github.aieducationbackend.entity.User;
import io.github.aieducationbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register/save")
    public ResponseEntity<UserDto> registration(UserDto userDto) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            ResponseEntity.noContent();
        }

        userService.saveUser(userDto);
        return ResponseEntity.ok(userDto);
    }
}

