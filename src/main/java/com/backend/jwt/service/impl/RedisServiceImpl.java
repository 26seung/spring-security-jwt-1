package com.backend.jwt.service.impl;

import com.backend.jwt.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void 값입력(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    // 만료시간 설정 -> 자동 삭제
    public void 만료시간(String key, String value, long timeout){
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    public String 값찾기(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void 값삭제(String key) {
        redisTemplate.delete(key);
    }
}
