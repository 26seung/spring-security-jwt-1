package com.backend.jwt.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JwtTokenDto {

    private String accessToken;
    private String expireTime;


}
