package com.backend.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.jwt.config.auth.PrincipalDetails;
import com.backend.jwt.config.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {


    private final CookieUtils cookieUtils;
    //  AccessToken 생성
    public String generateAccessToken(String username){

        Date expireTime = new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_EXPIRATION_TIME);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(expireTime)
                .withClaim("username", username)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
//    public String generateAccessToken(PrincipalDetails principalDetails){
//
//        Date expireTime = new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_EXPIRATION_TIME);
//
//        return JWT.create()
//                .withSubject(principalDetails.getUsername())
//                .withIssuedAt(new Date())
//                .withExpiresAt(expireTime)
//                .withClaim("id", principalDetails.getUser().getId())
//                .withClaim("username", principalDetails.getUser().getUsername())
//                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
//    }
    //  RefreshToken 생성
    public String generateRefreshToken(){

        Date expireTime = new Date(System.currentTimeMillis()+ JwtProperties.REFRESH_EXPIRATION_TIME);
        String refreshTokenId = String.valueOf(UUID.randomUUID());

        return JWT.create()
                .withSubject(refreshTokenId)
                .withIssuedAt(new Date())
                .withExpiresAt(expireTime)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    //  withSubject 에 넣어주었던 username 아이디 값을 불러온다..
    public String getUserId(String jwtToken){
        return JWT.decode(jwtToken).getSubject();
    }
    //  토큰 생성시 설정된 withExpiresAt 만료시간 값을 가져온다.
    public Date getExpiredTime(String jwtToken){
        return JWT.decode(jwtToken).getExpiresAt();
    }

    //  refreshToken 생성시 넣어둔 UUID 값을 가져온다..
    public String getRefreshTokenId(String token) {
        return JWT.decode(token).getSubject();
    }
    //  넘어온 토큰값과 db에 저장된 토근의 uuid 값을 비교한다.
    public boolean equalsRefreshTokenId(String refreshToken, String refreshTokenId){
        String compareToken = getRefreshTokenId(refreshToken);
        return refreshTokenId.equals(compareToken);
    }

    //  토큰 생성시 사용 메서드
    public String createToken(PrincipalDetails principalDetails){

        Date expireTime = new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_EXPIRATION_TIME);

        return JWT.create()
                .withSubject(principalDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(expireTime)
//                .withClaim("id", principalDetails.getUser().getId())
//                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    //  토큰 유효성 검사
    public boolean validateToken(String jwtToken){
//        System.out.println("validateToken : " + JWT.decode(jwtToken).getExpiresAt());
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken);
            //  decode 옵션은 토큰의 정보만 알려주고 검증은 해주지 않기 때문에, require 옵션을 사용하여 토큰 값을 검증
            //  DecodedJWT decodedJWT = JWT.decode(jwtToken);
            log.info("============================================");
            log.info("jwt Token       : " + decodedJWT.getToken());
            log.info("jwt Id          : " + decodedJWT.getId());
            log.info("jwt Subject     : " + decodedJWT.getSubject());
            log.info("jwt IssuedAt    : " + decodedJWT.getIssuedAt());
            log.info("jwt ExpiresAt   : " + decodedJWT.getExpiresAt());
            log.info("jwt decodedJWT   : " + decodedJWT.getClaims());
            log.info("============================================");
            return true;

        }catch (JWTVerificationException e){
            log.error("JWTVerificationException : " + e.getMessage());
        }
        return false;
    }
}
