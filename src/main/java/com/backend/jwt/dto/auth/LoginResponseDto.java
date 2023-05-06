package com.backend.jwt.dto.auth;

import lombok.Getter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private String username;
    private String accessToken;
    private String refreshToken;


}
