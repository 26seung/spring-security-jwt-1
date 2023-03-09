package com.backend.jwt.config.auth;

import com.backend.jwt.domain.User;
import com.backend.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// 시큐리티 설정에서 loginProcessingUrl("/login"); 걸었기 때문에
// 로그인 요청이 오면 자동으로 UserDetailsService 타입으로 IOC 되어있는 `loadUserByUsername` 함수가 실행된다.


@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    // 시큐리티 Session (내부 Authentication(내부 UserDetails))
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("======> loadUserByUsername 의 username : " + username);
        User userEntity = userRepository.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException("User Not Found with username: " + username));
//
//        if (userEntity != null){
//            return new PrincipalDetails(userEntity);
//        }
        return new PrincipalDetails(userEntity);
    }
}
