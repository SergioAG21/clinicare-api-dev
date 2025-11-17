package com.sergioag.clinicare_api.dto.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}