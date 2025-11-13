package com.sergioag.clinicare_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.Gender;
import com.sergioag.clinicare_api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String address;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private Gender gender;
    private Role role;
    private String email;
}