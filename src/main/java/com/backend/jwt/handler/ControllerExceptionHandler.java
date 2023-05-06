package com.backend.jwt.handler;

import com.backend.jwt.dto.CMRespDto;
import com.backend.jwt.handler.ex.AccessTokenNotValidException;
import com.backend.jwt.handler.ex.CustomValidationException;
import com.backend.jwt.handler.ex.RefreshTokenValidationException;
import com.backend.jwt.utils.CookieUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class ControllerExceptionHandler {

    //  @ExceptionHandler(RuntimeException.class) 작성시  -> RuntimeException 이 발생하면 해당 메서드가 실행된다.
    //  수행 시 엄청나게 긴 오류문구가 아닌 String 타입의 getMessage 를 출력한다.
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationException(CustomValidationException e){
        return new ResponseEntity<>(new CMRespDto<>(-1,e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    //  refreshToken 이 유효하지 않을 경우에는 401에러와 함께 쿠키를 제거
    @ExceptionHandler(RefreshTokenValidationException.class)
    public ResponseEntity<?> refreshTokenValidationException(RefreshTokenValidationException e){
        //  쿠키 제거
        ResponseCookie responseCookie = new CookieUtils().removeRefreshTokenCookie();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(e.getMessage());
    }
    @ExceptionHandler(AccessTokenNotValidException.class)
    public ResponseEntity<?> accessTokenValidationException(AccessTokenNotValidException e){
        System.out.println("AccessTokenNotValidExceptionAccessTokenNotValidException");
        //  쿠키 제거
//        ResponseCookie responseCookie = new CookieUtils().removeRefreshTokenCookie();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
