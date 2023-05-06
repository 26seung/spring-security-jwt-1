package com.backend.jwt.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.backend.jwt.config.jwt.JwtProperties;
import com.backend.jwt.domain.RefreshToken;
import com.backend.jwt.domain.User;
import com.backend.jwt.dto.auth.JwtTokenDto;
import com.backend.jwt.handler.ex.AccessTokenNotValidException;
import com.backend.jwt.handler.ex.CustomValidationException;
import com.backend.jwt.handler.ex.RefreshTokenValidationException;
import com.backend.jwt.repository.RefreshTokenRepository;
import com.backend.jwt.repository.UserRepository;
import com.backend.jwt.service.RefreshTokenService;
import com.backend.jwt.utils.CookieUtils;
import com.backend.jwt.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String > redisTemplate;

    @Override
    public String 로그아웃(String accessToken){
        String logoutToken = getAccessToken(accessToken);
        if (!jwtUtils.validateToken(logoutToken)){
            throw new AccessTokenNotValidException("accessToken 값이 유효하지 않습니다.");
        }
        System.out.println("aaacess : " + logoutToken);
        RefreshToken refreshToken = refreshTokenRepository.findById(jwtUtils.getUserId(logoutToken))
                .orElseThrow(()-> new RefreshTokenValidationException("redis 서버 내에서 refreshToken 값을 찾을 수 없습니다."));

        refreshTokenRepository.delete(refreshToken);
        //  빈 값의 cookie 를 생성하여 기존 쿠키 값 삭제
        ResponseCookie responseCookie = cookieUtils.removeRefreshTokenCookie();

        return responseCookie.toString();
}

    @Override
    public void 토큰저장(String username, String uuid) {

        if (!userRepository.findByUsername(username).isPresent()){
            throw new UsernameNotFoundException("해당 아이디 : " + username + " 는 없는 사용자입니다.");
        }
        //  JPA repository 사용
        //  username = 유저Id , uuid = 리프레시토큰Id
        refreshTokenRepository.save(new RefreshToken(username, uuid));

        //  redisTemplate 사용시 방법
//        redisTemplate.opsForValue().set(username,uuid,10000, TimeUnit.MILLISECONDS);
//        System.out.println("redisTemplate : "  + redisTemplate.opsForValue().get(username));

    }

    //  1. accessToken 이 만료된 경우 새로운 토큰이 발급되어야 한다.
    //  2. request 로 전달 받은 accessToken , refreshToken 에 대한 유효성 검사를 진행하고 유효하지 않다면 `401 에러`를 반환한다.
    //  3. refreshToken 이 유효하다면 redis 에 저장되어있는 (즉, 로그인 시 저장된) refresh token (uuid) 값과 동일한지 확인합니다.
    //  4. 새로 발급된 accessToken 과 유효기간을 response body 에 넣어 전달한다.
    //  5. refreshToken 의 경우 cookie 의 httpOnly 속성을 true 로 설정하여 넣어 전달한다.
    @Override
    public JwtTokenDto 토큰재발급(String accessToken, String refreshToken) {
        System.out.println("토큰재발급1 accessToken : " + accessToken);

        //  해당 accessToken 에서 로그인아이디 값 추출
        //  redis 에 해당 accessToken 에 대한 로그인아이디 값이 저장되어 있는지 확인
        String username = jwtUtils.getUserId(getAccessToken(accessToken));
        RefreshToken findRefreshToken = refreshTokenRepository.findById(username)
                .orElseThrow(()-> new RefreshTokenValidationException("redis 에 등록된 해당 유저 (" + username + ")를 찾을 수 없습니다."));


        //  refreshToken 의 유효성검사.
        //  1. JWTDecode 를 통해 refreshToken 정보가 유효한지를 확인
        //  2. request 로 넘어온 refreshToken 의 id 값과 redis 데이터베이스에 저장되어 있는 (uuid) 값이 일치하는지를 비교
        String findRefreshTokenId = findRefreshToken.getRefreshTokenId();
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.equalsRefreshTokenId(refreshToken, findRefreshTokenId)){
            refreshTokenRepository.delete(findRefreshToken);
            throw new RefreshTokenValidationException(refreshToken, "유효한 refreshToken 값이 아닙니다.");
        }

        //  새로운 accessToken 재발급
        String newAccessToken = jwtUtils.generateAccessToken(username);
        Date expiredTime = jwtUtils.getExpiredTime(newAccessToken);

        return JwtTokenDto.builder()
                .accessToken(newAccessToken)
                .expireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiredTime))
                .build();
    }

    @Override
    public void 토큰검증(String accessToken) {

//        String tokenHeader = accessToken.replace(JwtProperties.TOKEN_PREFIX,"");
        if (!jwtUtils.validateToken(getAccessToken(accessToken))){
            throw new RefreshTokenValidationException("accessToken 값이 유효하지 않습니다.");
        }
    }

    //  넘어오는 accessToken 값에서 Bearer 제거
    private String getAccessToken(String accessToken){
        return accessToken.replace(JwtProperties.TOKEN_PREFIX,"");
    }
}
