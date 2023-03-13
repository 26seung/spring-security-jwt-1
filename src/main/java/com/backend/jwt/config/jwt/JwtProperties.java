package com.backend.jwt.config.jwt;

public interface JwtProperties {

    String SECRET = "Euseung"; // 우리 서버만 알고 있는 비밀값
    int ACCESS_EXPIRATION_TIME = 1000 * 60 ; // 1000 * 60 * 60 = 1시간 , 864000000 = 10일 (1/1000초)
    int REFRESH_EXPIRATION_TIME = 1000 * 60 ;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

}
