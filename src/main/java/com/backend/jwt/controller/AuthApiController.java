package com.backend.jwt.controller;

import com.backend.jwt.config.auth.PrincipalDetails;
import com.backend.jwt.domain.ERole;
import com.backend.jwt.domain.User;
import com.backend.jwt.handler.ex.CustomValidationException;
import com.backend.jwt.repository.UserRepository;
import com.backend.jwt.service.AuthService;
import com.backend.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = "*", maxAge = 3000)
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthApiController {


    private final AuthService authService;
    private final UserRepository userRepository;


    @GetMapping("/")
    public @ResponseBody String info(){
        return "Index";
    }

    @GetMapping("/user")
    public User user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("유저페이지 실행 : " + principalDetails);
        return principalDetails.getUser();
    }
    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("관리자 페이지 실행 : " + principalDetails);
        return "admin";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        System.out.println("==> Login : " + user);
        return authService.로그인(user);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody User user, BindingResult bindingResult){
        //  전처리 예외처리 를 위하여 `@Valid 어노테이션과 BindingResult` 을 사용하여 SQL 문 오류를 뱉기전에 전처리 해준다..
        //  프론트 단에서도 별도의 예외처리를 진행하고, 백단에서도 예외를 처리하여 보안을 강화한다. (혹시나 허용하지 않은 API 요청을 통한 접근에 대한 문제를 막아준다.)
        if (bindingResult.hasErrors()){
            System.out.println("===========================================================");
            System.out.println("==> Join bindingResult: (" + bindingResult.hasErrors() + ")  /  message : " + bindingResult.getFieldError().getDefaultMessage());
            System.out.println("===========================================================");

            Map<String ,String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMap.put(error.getField(),error.getDefaultMessage());
                System.out.println("==> Join FieldError: (" + error.getDefaultMessage() + ")");

            }
            System.out.println("===========================================================");
            throw new CustomValidationException("유효성검사에 실패하였습니다. (전처리작업 오류)",errorMap);
        }
        System.out.println("==> Join : " + user);
        return authService.회원가입(user);
    }
}
