package com.sergioag.clinicare_api.entity;

import static jakarta.persistence.GenerationType.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.Gender;
import com.sergioag.clinicare_api.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "userRoles")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String dni;

    @NotBlank
    private String name;

    @NotBlank
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @NotBlank
    private String address;

    @NotBlank
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Email
    private String email;

    @NotBlank
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // Evita LazyInitializationException usando EAGER
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();

}
