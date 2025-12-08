package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.auth.AuthRequest;
import com.sergioag.clinicare_api.dto.auth.AuthResponse;
import com.sergioag.clinicare_api.dto.auth.RegisterRequest;
import com.sergioag.clinicare_api.entity.Role;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.entity.UserRole;
import com.sergioag.clinicare_api.enums.UserStatus;
import com.sergioag.clinicare_api.exception.EmailNotFoundException;
import com.sergioag.clinicare_api.repository.RoleRepository;
import com.sergioag.clinicare_api.repository.UserRepository;
import com.sergioag.clinicare_api.security.JwtService;
import com.sergioag.clinicare_api.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Todo lo relacionado con la Autenticación")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login del Usuario y guarda el JWT")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailNotFoundException(
                        "El usuario con email no está registrado o no está confirmado"));

        // ExtraClaims con roles como antes
        Map<String, Object> extraClaims = new HashMap<>();
        UserStatus userStatus = (UserStatus) user.getStatus();
        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName()) // igual que antes
                .collect(Collectors.toSet());

        extraClaims.put("roles", roles);
        extraClaims.put("userStatus", userStatus);

        String token = jwtService.generateToken(request.getEmail(), extraClaims);
        return new AuthResponse(token);
    }


    @PostMapping("/register")
    @Operation(summary = "Registro de Usuario y manda un Email")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest req) {
        boolean emailExists = userRepository.findByEmail(req.getEmail().toLowerCase()).isPresent();
        boolean dniExists = userRepository.findByDni(req.getDni().toUpperCase()).isPresent();

        if (emailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El correo electrónico ingresado ya está en uso"));
        } else if (dniExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El DNI ingresado ya está en uso"));
        } else {
            // ************* USUARIO ***************
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
            user.setStatus(UserStatus.PENDING);

            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("ROLE USER not found"));

            // Crear UserRole para asignar el rol por defecto
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(defaultRole);

            user.setUserRoles(Set.of(userRole));

            // Envío del email
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("lastName", user.getLastName());
            variables.put("dni", user.getDni());
            variables.put("email", user.getEmail());

            try {
                emailService.sendEmail(
                        user.getEmail(),
                        "Hemos recibido tu solicitud de registro en CliniCare",
                        "registro-pendiente",
                        variables
                );
            } catch (Exception e) {
                System.err.println("Error al enviar el email: " + e.getMessage());
            }

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado correctamente"));
        }
    }


}
