package com.sergioag.clinicare_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.Gender;
import com.sergioag.clinicare_api.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String dni;
    private String name;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String address;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private UserStatus status;

    private Gender gender;

    private Set<String> roles;

    @JsonProperty("doctor_specialty")
    private String specialty;

    @JsonProperty("profile_user_image")
    private String profileImageUrl;
}
