package com.backend.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.backend.jwt.config.auth.PrincipalDetails;
import com.backend.jwt.domain.User;
import com.backend.jwt.repository.UserRepository;
import com.backend.jwt.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;


    //  인증이나 권한이 필요한 주소요청이 있을 시 해당 필터 매서드를 타게 된다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtAuthorizationFilter (doFilterInternal) ========================");
        System.out.println("인증 권한 doFilterInternal.getHeader : " + request.getHeader("Authorization"));
        System.out.println("인증 권한 doFilterInternal.getCookies : " + request.getCookies());
        System.out.println("JwtAuthorizationFilter (doFilterInternal) ========================");

        String jwt = jwtHeader(request);


        //  JWT 토큰을 검증해서 정상적인 사용자인지 확인
//        System.out.println("doFilterInternal 의 토큰 검증 jwt 1 ============================================ " + jwtUtils.validateToken(jwt));
//        System.out.println("doFilterInternal 의 토큰 검증 valid1 ============================================ " + jwtUtils.validateToken(jwt));
        if (jwt != null && jwtUtils.validateToken(jwt)){

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
            String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwt).getClaim("username").asString();
            System.out.println("doFilterInternal 의 토큰 검증 ID : " + username);
            //  서명이 정상적으로 되면,,,
            if (username != null) {
                User userEntity = userRepository.findByUsername(username).orElseThrow(() ->
                        new UsernameNotFoundException("해당 아이디 : " + username + " 는 없는 사용자입니다."));

                PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
                //  JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
                //  여기서는 이미 인증되었고 강제로 토큰을 생성하는 것이기 때문에 Pw 는  null 해주어도 상관 X
                //  (String username 의 build 작업에서 토큰에 대한 인증을 완료)
                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                //  강제로 시큐리티 세션에 접근하여 Authentication 객체를 저장한다.
                //  @AuthenticationPrincipal 를 통하여 정보를 가져오기 위해서 시큐리티 세션에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        System.out.println("JwtAuthorizationFilter (doFilterInternal) ------------------------------");
            //  응답을 안해주면 회원가입시에도 필터를 타기 때문에 진행되지 않음
            filterChain.doFilter(request, response);
    }

    //  해더값을 확인하여 토큰이 존재한다면, "Bearer " 를 제거한 토큰 값을 리턴 / 없다면 그냥 Null 값을 리턴
    private String jwtHeader(HttpServletRequest request){
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);

        if (StringUtils.hasText(jwtHeader) && jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)){
            return jwtHeader.replace(JwtProperties.TOKEN_PREFIX,"");
        }
        return null;
    }

}
