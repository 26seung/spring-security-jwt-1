package com.backend.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "refreshToken", timeToLive = 600)
public class RefreshToken {

    //  redis 경우 '스프링프레임위크' 에서 제공하는 @Id 어노테이션을 사용한다.
    @Id
    private String userId;
    private String refreshToken;

}
