package com.backend.jwt.config.jwt;

public interface JwtProperties {

    //  token 들의 만료시간
    Long ACCESS_EXPIRATION_TIME = 1000 * 60L; // 1000 * 60 * 60 = 1시간 , 864000000 = 10일 (1/1000초)
    Long REFRESH_EXPIRATION_TIME = 1000 * 60L ;    //  레디스는 60초 , 쿠키시간도 확인필요
    Long REDIS_REFRESH_EXPIRATION_TIME = 60L;       //  레디스는 초 단위로 설정됨

    //  token 암호 값 & 네이밍
    String SECRET = "Euseung"; // 우리 서버만 알고 있는 비밀값
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String REFRESH_COOKIE_NAME = "refresh-token";
}
