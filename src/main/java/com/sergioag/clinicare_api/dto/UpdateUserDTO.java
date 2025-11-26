package com.sergioag.clinicare_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String dni;
    private String name;
    private String lastName;
    private String email;
    private String address;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private String password;
    private Long specialtyId;

    @JsonProperty("profile_user_image")
    private String profileImageUrl;
}
