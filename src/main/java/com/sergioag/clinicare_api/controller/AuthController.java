package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.auth.AuthRequest;
import com.sergioag.clinicare_api.dto.auth.AuthResponse;
import com.sergioag.clinicare_api.dto.auth.RegisterRequest;
import com.sergioag.clinicare_api.entity.Role;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.enums.UserStatus;
import com.sergioag.clinicare_api.repository.RoleRepository;
import com.sergioag.clinicare_api.repository.UserRepository;
import com.sergioag.clinicare_api.security.JwtService;
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

        // extraClaims: puedes meter aquí roles u otros datos
        Map<String, Object> extraClaims = new HashMap<>();
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            var roles = userOpt.get().getRoles();
            extraClaims.put("roles", roles);
        }

        String token = jwtService.generateToken(request.getEmail(), extraClaims);
        return new AuthResponse(token);
    }

    // Endpoint de registro mínimo (opcional, útil para pruebas)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("ROLE USER not found"));

        UserStatus defaultStatus = UserStatus.PENDING;

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setDni(req.getDni()); // si tienes un campo en el DTO
        user.setAddress(req.getAddress());
        user.setLastName(req.getLastName());
        user.setBirthDate(req.getBirthDate());
        user.setName(req.getName());
        user.setGender(req.getGender());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRoles(Set.of(defaultRole));
        user.setStatus(defaultStatus);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), Map.of());

        return ResponseEntity.ok(token);
    }
}
