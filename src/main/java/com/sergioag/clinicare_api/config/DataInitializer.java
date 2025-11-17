package com.sergioag.clinicare_api.config;

import com.sergioag.clinicare_api.entity.Role;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.enums.Gender;
import com.sergioag.clinicare_api.enums.UserStatus;
import com.sergioag.clinicare_api.repository.RoleRepository;
import com.sergioag.clinicare_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String[] roles = { "ADMIN", "DOCTOR", "PATIENT", "USER" };

        // Creación de los roles
        for (String roleName : roles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }

        // Si no existe el admin
        if (userRepository.findByEmail("admin@clinicare.com").isEmpty()) {
            User admin = new User();
            admin.setName("Sergio");
            admin.setLastName("Alfaro Girón");
            admin.setEmail("admin@clinicare.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDni("49283726T");
            admin.setAddress("Avenida Rodriguez nº12, 2a");
            admin.setPhoneNumber("658372614");
            admin.setBirthDate(LocalDate.of(2001, 03, 04));
            admin.setGender(Gender.MALE);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setRoles(Set.of(roleRepository.findByName("ADMIN").get()));

            userRepository.save(admin);
            System.out.println("Usuario admin creado: **** admin@clinicare.com | admin123 ****");
        }
    }
}
