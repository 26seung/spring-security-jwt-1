package com.backend.jwt.service;

import com.backend.jwt.dto.auth.JwtTokenDto;

public interface RefreshTokenService {

    //  ResponseCookie 의 값을 String 으로 가져온다.
    String 로그아웃(String accessToken);
    //  유저 아이디 , refreshToken 생성시 uuid
    void 토큰저장(String username, String uuid);
    JwtTokenDto 토큰재발급(String accessToken, String refreshToken);
    void 토큰검증(String accessToken);

}
