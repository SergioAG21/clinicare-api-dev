package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.UpdateUserDTO;
import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.mapper.UserMapper;
import com.sergioag.clinicare_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de los Usuarios")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @GetMapping
    @Operation(summary = "Obtiene todos los Usuarios")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<User> users = userService.findAll();

        return ResponseEntity.ok(userMapper.toUserResponseDTOs(users));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtiene el usuario que ha iniciado la sesión")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        UserResponseDTO userResponse = userMapper.toUserResponseDTO(user);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("{id}")
    @Operation(summary = "Obtiene un Usuario por su ID")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        if (userService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toUserResponseDTO(userService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Guarda un Usuario")
    public ResponseEntity<?> save(@RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("{id}")
    @Operation(summary = "Actualiza un usuario por su ID")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        if (userService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PutMapping("{id}/roles")
    @Operation(summary = "Actualiza el rol del usuario por su ID")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long id, @RequestBody User userData) {
        if (userService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.updateUserRoles(id, userData));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Elimina el usuario por su ID")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (userService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/incomplete")
    @Operation(summary = "Cambia el estado del usuario a Incompleto")
    public ResponseEntity<?> makeUserIncomplete(@PathVariable Long id) {
        if (userService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        userService.makeUserIncomplete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/upload-image")
    @Operation(summary = "Subir imagen del usuario por su ID")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (!file.getContentType().startsWith("image")) {
                return ResponseEntity.badRequest().body("File must be of type image");
            }

            // Nombre del archivo
            String fileName = id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Carpeta uploads/users/
            Path uploadDir = Paths.get("uploads/users");
            Files.createDirectories(uploadDir);

            // Eliminar archivos antiguos del usuario
            Files.list(uploadDir)
                    .filter(path -> path.getFileName().toString().startsWith(id + "_"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });

            // Guardar el archivo
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Generar la url publica
            String publicUrl = "http://localhost:8080/uploads/users/" + fileName;

            // Guardarla en la BBDD
            User user = userService.findById(id);
            user.setProfileImageUrl(publicUrl);
            userService.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Imagen Subida Correctamente",
                    "url", publicUrl
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "En el campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
