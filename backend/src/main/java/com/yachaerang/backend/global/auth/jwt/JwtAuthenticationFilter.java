package com.yachaerang.backend.global.auth.jwt;

import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final MemberMapper memberMapper;
    private final JwtTokenProvider jwtTokenProvider;

    private static final List<AntPathMatcher> NON_FILTER_PATH = List.of(
            new AntPathMatcher("/test/**"),
            new AntPathMatcher("/api/v1/auth/**")
    );

    /*
    필터에서 제외할 PATH 정의
    제외 대상이면 true, 제외 대상이 아니라면 false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        for (AntPathMatcher matcher : NON_FILTER_PATH) {
            if (matcher.match(request.getServletPath(), request.getPathInfo())) {
                return false;
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
            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.UNAUTHORIZED_ACCESS);
        }
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
