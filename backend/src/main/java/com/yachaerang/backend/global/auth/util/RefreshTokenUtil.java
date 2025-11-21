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
    }

    // Redis 조회
    public String getRefreshToken(String memberCode) {
        return redisStringTemplate.opsForValue().get("refresh_token:" + memberCode);
    }

    // Redis 삭제
    public void deleteRefreshToken(String memberCode) {
        redisStringTemplate.delete(memberCode);
    }

    // Cookie에 추가
    public void addRefreshTokenCookie(HttpServletResponse response, String memberCode, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge((int) refreshTokenExpirationTime);
        response.addCookie(cookie);
    }

    // Cookie로부터 refresh token 추출
    public String getRefreshTokenCookie(HttpServletRequest request) {
        log.info("쿠키로부터 Refresh Token 찾기 시작...");
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            log.warn("쿠키가 비어 있습니다.");
            throw GeneralException.of(ErrorCode.COOKIE_NOT_FOUND);
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                String refreshToken = cookie.getValue();
                log.info("Refresh Token 발견. 유효성 검사 시작...");
                return refreshToken;
            }
        }
        throw GeneralException.of(ErrorCode.TOKEN_NOT_FOUND);
    }

    // Cookie로부터 MemberCode 조회
    public String getMemberCodeFromCookie(HttpServletRequest request) throws Exception {
        String refreshToken = getRefreshTokenCookie(request);

        if (jwtTokenProvider.validateToken(refreshToken)) {
            String memberCode = jwtTokenProvider.getMemberCodeFromToken(refreshToken);
            log.info("유효한 Refresh Token입니다. MemberCode: {}", memberCode);
            return memberCode;
        } else {
            log.warn("유효하지 않은 Refresh Token입니다.");
            throw GeneralException.of(ErrorCode.TOKEN_INVALID);
        }
    }

    // Cookie 삭제
    public void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
