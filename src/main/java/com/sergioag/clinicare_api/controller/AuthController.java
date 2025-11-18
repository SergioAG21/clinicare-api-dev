package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.auth.AuthRequest;
import com.sergioag.clinicare_api.dto.auth.AuthResponse;
import com.sergioag.clinicare_api.dto.auth.RegisterRequest;
import com.sergioag.clinicare_api.entity.Role;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.enums.UserStatus;
import com.sergioag.clinicare_api.exception.EmailAlredyInUseException;
import com.sergioag.clinicare_api.exception.EmailNotFoundException;
import com.sergioag.clinicare_api.repository.RoleRepository;
import com.sergioag.clinicare_api.repository.UserRepository;
import com.sergioag.clinicare_api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("El usuario con email no está registrado o no está confirmado"));

        // En extraClaims se puede meter lo que sea necesario
        Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", user.getRoles());

        String token = jwtService.generateToken(request.getEmail(), extraClaims);
        return new AuthResponse(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest req) {
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("ROLE USER not found"));

        boolean emailExists = userRepository.findByEmail(req.getEmail().toLowerCase()).isPresent();
        boolean dniExists = userRepository.findByDni(req.getDni().toUpperCase()).isPresent();

        if (emailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El correo electrónico ingresado ya está en uso"));
        } else if (dniExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El DNI ingresado ya está en uso"));
        } else {
            User user = new User();
            user.setEmail(req.getEmail());
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setDni(req.getDni());
            user.setAddress(req.getAddress());
            user.setLastName(req.getLastName());
            user.setBirthDate(req.getBirthDate());
            user.setName(req.getName());
            user.setGender(req.getGender());
            user.setPhoneNumber(req.getPhoneNumber());
            user.setRoles(Set.of(defaultRole));
            user.setStatus(UserStatus.PENDING);

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado correctamente"));
        }
    }

}
