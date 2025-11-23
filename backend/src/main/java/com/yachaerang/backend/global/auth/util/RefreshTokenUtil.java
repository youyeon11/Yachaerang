package com.yachaerang.backend.global.auth.util;

import com.yachaerang.backend.global.auth.jwt.JwtTokenProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUtil {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpirationTime;

    public void saveRefreshToken(String memberCode, String refreshToken) {
        String key = "refresh_token:" + memberCode;
        redisStringTemplate.opsForValue().set(key, refreshToken);
        redisStringTemplate.expire(key, refreshTokenExpirationTime, TimeUnit.SECONDS);
    }

    // Redis 조회
    public String getRefreshToken(String memberCode) {
        return redisStringTemplate.opsForValue().get("refresh_token:" + memberCode);
    }

    // Redis 삭제
    public void deleteRefreshToken(String memberCode) {
        redisStringTemplate.delete(memberCode);
    }
}
