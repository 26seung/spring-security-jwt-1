package com.backend.jwt.config.jwt;

public interface JwtProperties {

    //  token 들의 만료시간
    Long ACCESS_EXPIRATION_TIME = 1000 * 60L; // 1000 * 60 * 60 = 1시간 , 864000000 = 10일 (1/1000초)
    Long REFRESH_EXPIRATION_TIME = 1000 * 60L ;

    //  token 암호 값 & 네이밍
    String SECRET = "Euseung"; // 우리 서버만 알고 있는 비밀값
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String REFRESH_COOKIE_NAME = "refresh-token";
}
