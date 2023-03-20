package com.backend.jwt.utils;

import com.backend.jwt.config.jwt.JwtProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class CookieUtils {

    public String getJwtCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, "jwtCookie");
        if (cookie != null){
            return cookie.getValue();
        }else {
            return null;
        }
    }
    public ResponseCookie generateRefreshTokenCookie(String refreshToken){
        return ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(JwtProperties.REFRESH_EXPIRATION_TIME)
                .build();
    }
    //  refreshCookie 빈값의 쿠키를 전송하여 기존 값을 제거한다.
    public ResponseCookie removeRefreshTokenCookie() {
        return ResponseCookie.from(JwtProperties.REFRESH_COOKIE_NAME, null)
                .maxAge(0)
                .path("/")
                .build();
    }

    public ResponseCookie generateCookie(String name, String value, String path){
        return ResponseCookie.from(name, value).path(path).httpOnly(true).secure(false).maxAge(JwtProperties.REFRESH_EXPIRATION_TIME).build();
    }
    public Cookie of(ResponseCookie responseCookie) {
        Cookie cookie = new Cookie(responseCookie.getName(), responseCookie.getValue());
        cookie.setPath(responseCookie.getPath());
        cookie.setSecure(responseCookie.isSecure());
        cookie.setHttpOnly(responseCookie.isHttpOnly());
        cookie.setMaxAge((int) responseCookie.getMaxAge().getSeconds());
        return cookie;
    }


}
