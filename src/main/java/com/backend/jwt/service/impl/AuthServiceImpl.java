package com.backend.jwt.service.impl;

import com.backend.jwt.domain.ERole;
import com.backend.jwt.domain.User;
import com.backend.jwt.handler.ex.CustomValidationException;
import com.backend.jwt.repository.UserRepository;
import com.backend.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public void 회원가입(User user){
        System.out.println("회원가입 서비스 시작 : " + user);
        //  이미 가입처리된 아이디가 존재하는지 확인하여 예외처리
        //  @Column(unique = true) 은 BindingResult 에서 캐치 하지 못하므로 AOP 발동되지 않음
        if (userRepository.existsByUsername(user.getUsername())){
            throw new CustomValidationException("중복된 아이디 (" + user.getUsername() +") 가 존재합니다.");
        }
        System.out.println("회원가입 서비스 중반");

        //  빌더 패턴을 이용하여 userEntity 를 생성하여 JPA 등록 진행
        //  ID 는 SQL 등록시에 자동으로 순번이 매겨진다...
        User userEntity = user.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .eRole(ERole.ROLE_USER_EU)
                .build();

        userRepository.save(userEntity);
        System.out.println("회원가입 서비스 종료 : " + userEntity);
    }
}
