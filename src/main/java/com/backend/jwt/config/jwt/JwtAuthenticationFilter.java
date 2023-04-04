package com.backend.jwt.config.jwt;

import com.backend.jwt.config.auth.PrincipalDetails;
import com.backend.jwt.domain.User;
import com.backend.jwt.dto.auth.LoginRequestDto;
import com.backend.jwt.service.RefreshTokenService;
import com.backend.jwt.utils.CookieUtils;
import com.backend.jwt.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final RefreshTokenService refreshTokenService;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중");
        ObjectMapper om = new ObjectMapper();
        try {
            LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
            System.out.println("JwtAuthenticationFilter_user:: " + loginRequestDto);

            //  유저네임패스워드 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),loginRequestDto.getPassword());

            // PrincipalDetailsService 의 loadUserByUsername() 함수가 실행된다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            System.out.println("==================================================================");
            PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
            System.out.println("attemptAuthentication (authenticationToken) : " + authenticationToken);
            System.out.println("attemptAuthentication (principalDetails) : " + principalDetails);
            System.out.println("==================================================================");
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //  attemptAuthentication 싫행 후 요청이 정상적으로 진행되면 successfulAuthentication 함수가 실행된다.
    //  JWT 토큰을 생성하여 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

//        PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
//        System.out.println("principalDetails ---------: " + principalDetails.getUsername());
        String getUsername = ((PrincipalDetails) authentication.getPrincipal()).getUsername();
        System.out.println("======[authentication]=============================" + ((PrincipalDetails) authentication.getPrincipal()).getUsername());
        //  쿠키 미사용시
        String accessToken = jwtUtils.generateAccessToken(getUsername);
        Date expiredTime = jwtUtils.getExpiredTime(accessToken);
        System.out.println("successfulAuthentication 실행 (accessToken) : " + accessToken);
        System.out.println("successfulAuthentication 실행 (accessToken2) : " + jwtUtils.getUserId(accessToken));
        //  쿠키 사용시
        String refreshToken = jwtUtils.generateRefreshToken();
        System.out.println("successfulAuthentication 실행 (refreshToken) : " + jwtUtils.getUserId(refreshToken));

        //  refreshToken 의 Cookie 값을 생성
//        ResponseCookie responseCookie = cookieUtils.generateCookie(JwtProperties.REFRESH_COOKIE_NAME, refreshToken,"/");
        ResponseCookie responseCookie = cookieUtils.generateRefreshTokenCookie(refreshToken);
        response.setHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());

        //  redis 등록
//        String username = jwtUtils.getUserId(accessToken);
//        String uuid = jwtUtils.getUserId(refreshToken);
//        System.out.println(username + " / " + uuid);
        refreshTokenService.토큰저장(jwtUtils.getUserId(accessToken), jwtUtils.getUserId(refreshToken));
//        refreshTokenService.토큰저장(username,uuid);

        //  테스트 쿠키입니다.  //
        Cookie testCookie = new Cookie("cookie","value");
        testCookie.setPath("/");
        testCookie.setMaxAge(60);
        response.addCookie(testCookie);
        //  테스트 쿠키입니다.  //

        //  강제로 시큐리티 세션에 접근하여 Authentication 객체를 저장한다.
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.addHeader(JwtProperties.HEADER_STRING,JwtProperties.TOKEN_PREFIX + accessToken);

        //  위의 로직까지는 Body 값에는 데이터가 담기지 않고 Header 에만 값이 담기게 된다..
        //  Body 값에 토큰이나, 유저정보를 담기 위해서 `getWriter().write("")` 를 사용하여 전송할 데이터 값을 담아준다.
        //  전송하고자 하는 Body 값만 담아서 데이터를 넘겨준다..


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //  Map 을 사용하면 굳이 Dto 를 사용하지 않고 값을 전달 할 수 있다.
//        LoginResponseDto responseDto = new LoginResponseDto(principalDetails.getUsername(),accessToken,refreshToken);

        //  Java 9 이상부터 Map.of() 를 사용하여 간단하게 Map 을 초기화하여 사용할 수 있다.
        //  put(key, value) 형식을 사용하지 않고 그냥 key, value 형식으로 사용 가능. (# 다만 해당 인자는 10개 까지만 사용 가능, 11개는 오류)
        Map<String,Object> responseBody = Map.of(
                "accessToken", accessToken,
                "expiredTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiredTime)
        );

        //  데이터를 JSON 형태로 넘겨주어야 받기 용이하다
        String userEntity = new ObjectMapper().writeValueAsString(responseBody);
        PrintWriter out = response.getWriter();
        out.print(userEntity);
    }

}
