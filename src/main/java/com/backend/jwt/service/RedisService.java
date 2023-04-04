package com.backend.jwt.service;

public interface RedisService {
    void 값입력(String key, String value);
    void 만료시간(String key, String value, long timeout);
    String 값찾기(String key);
    void 값삭제(String key);
}
