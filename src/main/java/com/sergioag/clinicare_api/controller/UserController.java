package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.mapper.UserMapper;
import com.sergioag.clinicare_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService,  UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<User> users = userService.findAll();
        List<UserResponseDTO> usersResponse = userMapper.toUserResponseDTOs(users);
        return ResponseEntity.ok(usersResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        UserResponseDTO userResponse = userMapper.toUserResponseDTO(user);

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody User user, BindingResult result) {
        if(result.hasErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "En el campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
