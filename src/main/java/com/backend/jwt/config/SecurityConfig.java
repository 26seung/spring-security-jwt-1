package com.backend.jwt.config;

import com.backend.jwt.handler.jwt.AccessDeniedHandlerImpl;
import com.backend.jwt.handler.jwt.AuthenticationEntryPointImpl;
import com.backend.jwt.config.jwt.JwtAuthenticationFilter;
import com.backend.jwt.config.jwt.JwtAuthorizationFilter;
import com.backend.jwt.repository.UserRepository;
import com.backend.jwt.utils.CookieUtils;
import com.backend.jwt.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CorsConfig corsConfig;
    private final AuthenticationEntryPointImpl unauthorizedEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    //  시큐리티를 사용하기 위해서는 패스워드 암호화가 필요하다.
    //  bean 등롤을 통해 해당 메서드의 리턴되는 오브젝트를 IOC 로 등록해준다.


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilter())
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/user/**").authenticated()
                .antMatchers("/api/auth/manager/**").access("hasRole('ROLE_MANAGER_EU') or hasRole('ROLE_ADMIN_EU')")
                .antMatchers("/api/auth/admin/**").access("hasRole('ROLE_ADMIN_EU')")
                .anyRequest().permitAll();

        return http.build();
    }

    public class CustomFilter extends AbstractHttpConfigurer<CustomFilter,HttpSecurity>{
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtils, cookieUtils);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, userRepository, jwtUtils), UsernamePasswordAuthenticationFilter.class)
            ;

        }
    }


}
