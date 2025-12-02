package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.dto.UpdateUserDTO;
import com.sergioag.clinicare_api.entity.Role;
import com.sergioag.clinicare_api.entity.Specialty;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.entity.UserRole;
import com.sergioag.clinicare_api.enums.UserStatus;
import com.sergioag.clinicare_api.exception.EmailNotFoundException;
import com.sergioag.clinicare_api.repository.RoleRepository;
import com.sergioag.clinicare_api.repository.SpecialtyRepository;
import com.sergioag.clinicare_api.repository.UserRepository;
import com.sergioag.clinicare_api.repository.UserRoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SpecialtyRepository specialtyRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public UserServiceImpl(UserRepository userRepository, SpecialtyRepository specialtyRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.specialtyRepository = specialtyRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("El usuario con email no está registrado o no está confirmado"));
    }

    @Override
    public User update(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setDni(dto.getDni());
        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setStatus(UserStatus.ACTIVE);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Actualizar especialidad si se envía
        if (dto.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

            UserRole userRole = userRoleRepository.findByUserIdAndRoleName(id, "DOCTOR")
                    .orElseGet(() -> {
                        UserRole ur = new UserRole();
                        ur.setUser(user);
                        ur.setRole(roleRepository.findByName("DOCTOR")
                                .orElseThrow(() -> new RuntimeException("Rol DOCTOR no encontrado")));
                        return ur;
                    });

            userRole.setSpecialty(specialty);
            userRoleRepository.save(userRole);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserRoles(Long id, User newData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setDni(newData.getDni());
        user.setName(newData.getName());
        user.setLastName(newData.getLastName());
        user.setEmail(newData.getEmail());
        user.setAddress(newData.getAddress());
        user.setBirthDate(newData.getBirthDate());
        user.setGender(newData.getGender());
        user.setPhoneNumber(newData.getPhoneNumber());
        user.setStatus(UserStatus.INCOMPLETE);

        if (newData.getPassword() != null && !newData.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(newData.getPassword()));
        }

        user.getUserRoles().clear();

        if (newData.getUserRoles() != null) {
            for (UserRole ur : newData.getUserRoles()) {
                Role dbRole = roleRepository.findById(ur.getRole().getId())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + ur.getRole().getId()));

                UserRole newUr = new UserRole();
                newUr.setUser(user);
                newUr.setRole(dbRole);
                newUr.setSpecialty(ur.getSpecialty());

                user.getUserRoles().add(newUr);
            }
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("dni", user.getDni());
        variables.put("name", user.getName());
        variables.put("lastName", user.getLastName());
        variables.put("email", user.getEmail());
        variables.put("address", user.getAddress());
        variables.put("birthDate", user.getBirthDate());
        variables.put("gender", user.getGender());
        variables.put("phoneNumber", user.getPhoneNumber());

        // Traducir mediante un map
        Map<String, String> roleTranslations = Map.of(
                "DOCTOR", "Doctor",
                "PATIENT", "Paciente",
                "ADMIN", "Administrador"
        );

        // Extraer nombres de roles desde userRoles y traducirlos
        Set<String> translatedRoles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())               // obtenemos el nombre del rol
                .map(roleName -> roleTranslations.getOrDefault(roleName, roleName)) // traducimos
                .collect(Collectors.toSet());

        variables.put("roles", translatedRoles);

        // Envío del email
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Se ha verificado tu registro en CliniCare",
                    "bienvenida",
                    variables
            );
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        id = this.findById(id).getId();
        userRepository.deleteById(id);
    }

    public User makeUserIncomplete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setStatus(UserStatus.INCOMPLETE);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignPatientToDoctor(Long patientId, Long doctorId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado con id: " + patientId));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("Doctor no encontrado con id: " + doctorId));

        // Validar roles
        boolean isPatient = patient.getUserRoles().stream()
                .anyMatch(ur -> "PATIENT".equalsIgnoreCase(ur.getRole().getName()));
        boolean isDoctor = doctor.getUserRoles().stream()
                .anyMatch(ur -> "DOCTOR".equalsIgnoreCase(ur.getRole().getName()));

        if (!isPatient) {
            throw new IllegalArgumentException("El usuario no es un paciente");
        }
        if (!isDoctor) {
            throw new IllegalArgumentException("El usuario no es un doctor");
        }

        // Añadir paciente al doctor y viceversa
        doctor.getPatients().add(patient);
        patient.getDoctors().add(doctor);

        // Guardar ambos
        userRepository.save(doctor);
        userRepository.save(patient);
    }

    @Override
    public List<User> findDoctorBySpecialtyId(Long specialtyId) {
        Long doctorRoleId = roleRepository.findByName("DOCTOR")
                .orElseThrow(() -> new RuntimeException("Rol DOCTOR no encontrado"))
                .getId();

        return userRepository.findDoctorsBySpecialty(specialtyId, doctorRoleId);
    }
}
