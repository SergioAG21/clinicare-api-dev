package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.UserLoginDTO;
import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.mapper.UserMapper;
import com.sergioag.clinicare_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthController(UserService userService,  UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO request) {
        User user = userService.findByEmailAndPassword(request.getEmail(), request.getPassword())
                .orElseThrow(() -> new RuntimeException("El correo electrónico o la contraseña que ingresaste no son correctos. Por favor, verifica tus credenciales e inténtalo nuevamente."));
        return ResponseEntity.ok(userMapper.toUserResponseDTO(user));
    }
}
