package com.backend.jwt.dto.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}