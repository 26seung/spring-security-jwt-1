package com.backend.jwt.handler;

import com.backend.jwt.dto.CMRespDto;
import com.backend.jwt.handler.ex.CustomValidationException;
import com.backend.jwt.handler.ex.RefreshTokenValidationException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(RefreshTokenValidationException.class)
    public ResponseEntity<?> refreshTokenValidationException(RefreshTokenValidationException e){
        return new ResponseEntity<>(new CMRespDto<>(-1, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }
}
