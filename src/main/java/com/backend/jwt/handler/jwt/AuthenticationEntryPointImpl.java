package com.backend.jwt.handler.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//  @Component 어노테이션을 통해 Bean 으로 등록해준다.
//  AuthenticationEntryPoint 가 등록되지 않으면  403 Forbidden 오류를 반환해주기 때문에
//  이를 통해 익명의 사용자가 접근시 401 오류를 반환해주도록 처리한다.
//  유효한 자격증명을 제공하지 않고 접근하려 할때 401
@Component
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
