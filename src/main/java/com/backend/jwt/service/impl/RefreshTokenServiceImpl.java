package com.backend.jwt.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.backend.jwt.config.jwt.JwtProperties;
import com.backend.jwt.domain.RefreshToken;
import com.backend.jwt.domain.User;
import com.backend.jwt.dto.auth.JwtTokenDto;
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


    public ResponseEntity<?> refreshToken1(String username, String subject){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->
                        new UsernameNotFoundException("해당 아이디 : " + username + " 는 없는 사용자입니다."));
//        refreshTokenRepository.save();
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, "").body("");
    }
    public ResponseEntity<?> refreshJwtToken(String accessToken, String refreshToken){
        String userId = jwtUtils.getUserId(accessToken);

        RefreshToken findRefreshToken = refreshTokenRepository.findById(userId)
                .orElseThrow(()->
                        new CustomValidationException("해당 사용자 (" + userId + ")와 일치하는 refreshToken 값을 찾을 수 없습니다." ));

//        findRefreshToken.getRefreshToken();

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, "").body("");
    }
    public String 로그아웃(String accessToken){

        if (!jwtUtils.validateToken(accessToken)){
            throw new CustomValidationException("accessToken 값이 유효하지 않습니다.");
        }
//
        RefreshToken refreshToken = refreshTokenRepository.findById(jwtUtils.getUserId(accessToken))
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
        //  username = 유저Id , uuid = 리프레시토큰Id
        //  repository 사용
        RefreshToken refreshToken = new RefreshToken(username,uuid);
        refreshTokenRepository.save(refreshToken);
        System.out.println("토큰등록 값 조회 : " + refreshTokenRepository.findByRefreshTokenId(uuid));
        System.out.println("토큰등록 값 조회 : " + refreshTokenRepository.findById(username).get().getRefreshTokenId());

        //  redisTemplate 사용
//        redisTemplate.opsForValue().set(username,uuid,10000, TimeUnit.MILLISECONDS);
//        System.out.println("redisTemplate : "  + redisTemplate.opsForValue().get(username));

    }

    @Override
    public JwtTokenDto 토큰재발급(String accessToken, String refreshToken) {
        System.out.println("토큰재발급1");

        String username = jwtUtils.getUserId(getAccessToken(accessToken));
        RefreshToken findRT = refreshTokenRepository.findById(username)
                .orElseThrow(()-> new RefreshTokenValidationException("redis 에 등록된 해당 유저 (" + username + ")를 찾을 수 없습니다."));

        String refreshTokenId = findRT.getRefreshTokenId();
        System.out.println("RTokenId : " + refreshTokenId);
        System.out.println("jwtUtils.getRefreshTokenId(refreshToken) : " + jwtUtils.getRefreshTokenId(refreshToken));
        System.out.println("===================================== ");
        System.out.println("refreshToken : " + refreshToken);

        //  리프레시토큰 검증, 1. JWTDecode 를 통해 리프레시토큰의 정보가 유효한지를 확인
        //  2. request 로 넘어온 리프레시토큰 값에서 가져온 (uuid) 값과 redis 데이터베이스에 저장되어 있는 (uuid) 값이 일치하는지를 비교
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.equalsRefreshTokenId(refreshToken, refreshTokenId)){
            System.out.println("validateToken test");
            refreshTokenRepository.delete(findRT);
            throw new RefreshTokenValidationException(refreshToken, "유효한 RT 값이 아닙니다.");
        }

        //  accessToken 재발급
        String newAccessToken = jwtUtils.generateAccessToken(username);
        Date expiredTime = jwtUtils.getExpiredTime(newAccessToken);
        System.out.println("NOoooooooo");

        JwtTokenDto jwtTokenDto = JwtTokenDto.builder()
                .accessToken(newAccessToken)
                .expireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiredTime))
                .build();

        return jwtTokenDto;
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
