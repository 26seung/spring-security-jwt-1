package com.backend.jwt.controller;

import com.backend.jwt.config.auth.PrincipalDetails;
import com.backend.jwt.domain.User;
import com.backend.jwt.repository.UserRepository;
import com.backend.jwt.service.AuthService;
import com.backend.jwt.service.impl.AuthServiceImpl;
import com.backend.jwt.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//@CrossOrigin(origins = "*", maxAge = 3000)
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthApiController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
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

    //  Login 경로는 클래스 JwtAuthenticationFilter (attemptAuthentication) 메서드 에서 자동 수행

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody User user, BindingResult bindingResult){
        //  전처리 예외처리 를 위하여 `@Valid 어노테이션과 BindingResult` 을 사용하여 SQL 문 오류를 뱉기전에 전처리 해준다..
        //  프론트 단에서도 별도의 예외처리를 진행하고, 백단에서도 예외를 처리하여 보안을 강화한다. (혹시나 허용하지 않은 API 요청을 통한 접근에 대한 문제를 막아준다.)

        System.out.println("==> Join Controller : " + user);
        authService.회원가입(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료 되었습니다.");
    }

    //  refreshToken 을 사용한 로직

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken){
        System.out.println("logout accessToken: " + accessToken);
        String responseCookie = refreshTokenService.로그아웃(accessToken);
        System.out.println("logout responseCookie: " + responseCookie);
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE,responseCookie).body("로그아웃");
    }
    @PostMapping("/reissue")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String accessToken,
                                          @CookieValue(value = "refresh-token") String refreshToken){
        refreshTokenService.토큰발급(accessToken,refreshToken);
        return ResponseEntity.status(HttpStatus.OK).header("").body("");
    }
    //  헤더가 Authorization 로 넘어오는 값을 validation. (JWT 의 decode) 하여 해당 토큰이 아직 유효한지를 재검증
    @PostMapping("/check")
    public ResponseEntity<?> checkAccessToken(@RequestHeader("Authorization") String accessToken){
        Boolean check = refreshTokenService.토큰검증(accessToken);
        System.out.println("check 실행 .. " + check );
        return ResponseEntity.status(HttpStatus.OK).body("토큰검증이 완료되었습니다");
    }
}
