package com.yachaerang.backend.global.auth.jwt;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    private static final List<String> NON_FILTER_PATTERNS = List.of(
            "/test/**",
            "/api/v1/auth/**",
            "/api/v1/products/**",
            "/api/v1/daily-prices/**"
    );

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /*
    필터에서 제외할 PATH 정의
    제외 대상이면 true, 제외 대상이 아니라면 false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUrl = request.getRequestURI();

        log.debug("Request URL in JwtFilter: {}", requestUrl);

        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestUrl.startsWith(contextPath)) {
            requestUrl = requestUrl.substring(contextPath.length());
        }
        for (String pattern: NON_FILTER_PATTERNS) {
            if (PATH_MATCHER.match(pattern, requestUrl)) {
                return true;
            }
        }
        return false;
    }

    /*
    동일한 요청 별로 실행할 필터 정의
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Verify
        try {
            String token = getJwtFromRequestHeader(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 인증 실패 시 SecurityContext를 비움
            SecurityContextHolder.clearContext();
            log.warn("JWT 인증 필터 실패: {}", e.getMessage());
            throw GeneralException.of(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        filterChain.doFilter(request, response);
    }

    /*
    요청 헤더를 통하여 토큰의 정보를 추출
     */
    private String getJwtFromRequestHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        } else if (bearerToken != null) {
            return bearerToken;
        }
        return null;
    }

}
