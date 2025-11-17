package com.sergioag.clinicare_api.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.Gender;
import com.sergioag.clinicare_api.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String name;

        @NotBlank
        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("birth_date")
        private LocalDate birthDate;

        @NotBlank
        private String dni;

        @NotBlank
        private String address;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @NotNull
        private Gender gender;

        @NotNull
        private UserStatus status;
    }
