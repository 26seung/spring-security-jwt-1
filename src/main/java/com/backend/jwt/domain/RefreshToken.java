package com.backend.jwt.domain;

import com.backend.jwt.config.jwt.JwtProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refresh_token")
public class RefreshToken {

    //  redis 경우 '스프링프레임위크' 에서 제공하는 @Id 어노테이션을 사용한다.
    @Id
    private String userId;
    @Indexed
    private String refreshTokenId;
    @TimeToLive
    private Long expireTime;

    //  expireTime 값은 refreshToken 의 만료시간을 넣어준다.
    public RefreshToken(String userId, String refreshTokenId) {
        this.userId = userId;
        this.refreshTokenId = refreshTokenId;
        this.expireTime = JwtProperties.REDIS_REFRESH_EXPIRATION_TIME;
    }
}
