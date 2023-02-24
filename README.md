#### SecurityConfig

httpBasic() 이란 headers 에다 Authorization 이라는 Key 에 (Id, Pw) 값을 넣어 요청하는 방식이다.
중간에 id,pw 가 노출이 되는 위험이 있기 때문에 https 방식을 사용하여 암호화하여 사용해야한다.

httpBasic 방식은 (id,pw)를 사용하여 탈취에 대한 위험이 있기 때문에 상대적으로 안전한 토큰을 사용하여 이동시키는 Bearer 방식이 존재한다.

---

시큐리티 흐름도 

1. `SecurityConfig` 클래스에서 `@EnableWebSecurity` 주석을 통해 시큐리티를 활성화 한다.
2. 시큐리티에서 사용하는 속성인 `UserDetails` 을 상속받아 유저객체를 사용한다.
   - 상속받아 사용시 장점은 OAuth 인증시에도 별도 구축 필요가 없다.
3. `SecurityConfig` 클래스에서 페이지에 대한 권한처리 와 필터처리를 해줄수 있다.
   - 여기서 설정하는 필터는 기존 필터들보다 우선해서 실행된다.
4. 시큐리티에서 제공하는 `UsernamePasswordAuthenticationFilter` 를 통해서 (ID,PW) 확인하여 로그인을 진행
   - `AuthenticationManager` 를 받아와서 사용가능
   - 로그인 시도 시 `attemptAuthentication()` 함수를 실행한다.
   - 정상적이면 `UserDetailsService` 를 상속한 `PrincipalDetailsService` 클래스의 `loadUserByUsername()` 함수가 실행된다.
   - `UserDetails` 을 세션에 담고 JWT 토큰을 생성하여 응답해주면 된다.
     - 세션에 담는 이유는 권한관리를 하기 위해서...
   - `UsernamePasswordAuthenticationToken` 에 username 와 password 를 담아서 `authenticationManager`를 실행하면 `loadUserByUsername()` 함수가 실행된다.
   - `Authentication` 을 담은 `authentication` 을 리턴 해주면 된다.
     - 리턴시 `authentication` 객체가 세션 영역에 저장된다.
     - `PrincipalDetailsService` 클래스의 `loadUserByUsername()` 함수가 실행후 정상이면 `authentication` 객체가 리턴됨
   - `attemptAuthentication()` 함수가 종료되면 `successfulAuthentication()` 함수가 실행된다.
     - JWT 토큰 사용시에는 토큰을 생성하여 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다.
   - `UsernamePasswordAuthenticationFilter` 의 `attemptAuthentication()` 함수는 `'/login'` 주소로의 요청시에만 실행되기에 api 실행시 별도 변경처리를 해주어야 한다.
     - `setFilterProcessesUrl("/api/auth/login");` 를 사용하여 요청 url을 변경해주었다.
- JWT 사용을 위해서 build 추가 필요 `implementation group: 'com.auth0', name: 'java-jwt', version: '4.2.1'` 빌드
- 유저 정보가 확인되면 빌더패턴을 사용한 토큰을 생성 (해쉬형태로 빌더) 

---

##### Optional 사용하여 Null 예외 처리

`UserRepository` 클래스 에서 
```
public User findByUsername(String username);  
이 아닌
Optional<User> findByUsername(String username);
```
- Optional 을 사용하는 이유는 Null 예외처리 발생 때문이다.
- public 으로 코드 생성하여 동작시 조회되지 않는 아이디로 로그인 시도시 `InternalAuthenticationServiceException` 오류가 발생하지만
```
User userEntity = userRepository.findByUsername(username).orElseThrow(()->
        new UsernameNotFoundException("User Not Found with username: " + username));
```
(Optional) 처리 시 예외오류가 발생하지 않도록 할 수 있다..

---

### 쿠키사용

1. 쿠키의 저장을 위해서는 클라이언트와 서버 모두 `Credentials` 부분을 `true`로 설정이 필요
   - 클라이언트와 http://localhost:3000 서버가 http://localhost:8080 서로 같은 Host이고 Port만 다른 셈이다.
   - 기본적으로 브라우저가 제공하는 요청 API 들은 별도의 옵션 없이 브라우저의 쿠키와 같은 인증과 관련된 데이터를 함부로 요청 데이터에 담지 않도록 되어있다. 이는 응답을 받을때도 마찬가지이다.
   - 프론트에서 axios 요청할 때, `withCredentials` 부분을 `true`로 해서 수동으로 CORS 요청에 쿠키값을 넣어줘야 한다.
2. HttpOnly 설정 사용시 자바스크립트 `document.cookie` 같은 문법으로 접근이 불가능하다. 
   - 보통의 쿠키만 콘솔창에 검색이 되는 모습을 볼 수 있다.
2. (의문) 크롬 옵션중에 (타사 쿠키 차단) 이 있는데, 해당 옵션 사용시 토큰 쿠키저장이 불가능 하였다. 그러면 로그인 수행이 불가능한지???
   - 재부팅후 수행시 쿠키 저장이 되네?


<img width="825" alt="image" src="https://user-images.githubusercontent.com/79305451/220356788-82752263-d884-4d49-8ad3-4c40a1871547.png">

---
