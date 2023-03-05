package com.backend.jwt.handler.aop;

import com.backend.jwt.handler.ex.CustomValidationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

//  AOP 처리를 위한 핸들러 @Aspect 처리
//  RestController, Service 모든 것들이 Component 를 상속해서 만들어져 있음

@Slf4j
@Component
@Aspect
public class ValidationAdvice {

    //  @Before, @After, @Around 등의 메서드를 사용하여 실행 순서를 정한다.

    @Around("execution(* com.backend.jwt.controller.*ApiController.*(..))")
    public Object advice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        //  ProceedingJoinPoint ==> 적용된 함수의 모든 곳에 접근할 수 있는 변수이며, 값이 담긴다.
        //  @Around("execution(..)") 를 통해 실행 적용된 함수보다 여기에 작성된 해당 로직이 먼저 실행
        //  그리고 proceedingJoinPoint.proceed() 를 통해 적용된 함수를 실행하는 순서이다.

        Object[] args = proceedingJoinPoint.getArgs();

        for (Object arg:args){
            if (arg instanceof BindingResult){
                BindingResult bindingResult = (BindingResult) arg;

                if (bindingResult.hasErrors()){
                    log.info("===========================================================");
                    log.info("Join bindingResult: (" + bindingResult.hasErrors() + ")  /  message : " + bindingResult.getFieldError().getDefaultMessage());
                    Map<String ,String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()){
                        errorMap.put(error.getField(),error.getDefaultMessage());
                    }

                    log.info(errorMap.toString());
                    log.info("===========================================================");
                    throw new CustomValidationException("유효성검사에 실패하였습니다. (전처리 작업)", errorMap);
                }
            }
        }

        return proceedingJoinPoint.proceed();
    }
}
