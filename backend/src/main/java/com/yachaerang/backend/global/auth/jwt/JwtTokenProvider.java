package com.yachaerang.backend.global.auth.jwt;

import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import io.jsonwebtoken.*;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/*
JWT 토큰을 담당하는 Provider Class
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final MemberMapper memberMapper;
    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.access.expiration}")
    private long accessTokenExpirationTime;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpirationTime;

    /*
    member의 email 기반으로 생성된 UUID를 기반으로 토큰을 생성
     */
    public String generateToken(Member member, long expirationTime, boolean isRefreshToken) {
        // token 생성
        try {
            Date now =  new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime * 1000);

            return Jwts.builder()
                    .setHeaderParam("type", isRefreshToken ? "refresh" : "access")
                    .setHeaderParam("algorithm", "HS256")
                    .setSubject(member.getMemberCode())
                    .claim("roles", member.getRole())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        }
        // 생성 실패
        catch (Exception e) {
            throw GeneralException.of(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    /*
    Access Token 생성
     */
    public String generateAccessToken(Member member) {
        return generateToken(member, accessTokenExpirationTime, false);
    }

    /*
    Refresh Token 생성
     */
    public String generateRefreshToken(Member member) {
        return generateToken(member, refreshTokenExpirationTime, true);
    }

    /*
    MemberCode 추출
     */
    public String getMemberCodeFromToken(String token) throws Exception {

        Claims claims = getClaimsFromToken(token);

        String memberCode = claims.getSubject(); // Subject로 등록한 memberCode

        if (!StringUtils.isEmpty(memberCode)) {
            throw GeneralException.of(ErrorCode.TOKEN_GENERATION_FAILED);
        }
        return memberCode;
    }

    /*
    반환을 UsernamePasswordAuthenticationToken의 인스턴스로 설정
     */
    public Authentication getAuthentication(String token) throws Exception {
        Claims claims = getClaimsFromToken(token);

        Member member = memberMapper.findByMemberCode(claims.getSubject());
        if (member == null) {
            throw GeneralException.of(ErrorCode.MEMBER_NOT_FOUND);
        }
        Role role = member.getRole();
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));

        // principal : member 객체
        return new UsernamePasswordAuthenticationToken(member, null, authorities);
    }

    /*
    Claims를 반환
     */
    private Claims getClaimsFromToken(String token) throws Exception {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();
    }

    /*
    JWT의 유효성 검증
     */
    public boolean validateToken(String token) throws Exception {
        try {
            final Claims claims = getClaimsFromToken(token);
            throw GeneralException.of(ErrorCode.CLAIM_EXTRACTION_FAILED);
        } catch (MalformedJwtException e) {
            LogUtil.warn("유효하지 않은 JWT token : {}", e.getMessage());
            throw GeneralException.of(ErrorCode.TOKEN_INVALID);
        } catch (SignatureException e) {
            LogUtil.warn("유효하지 않은 서명의 JWT token: {}", e.getMessage());
            throw GeneralException.of(ErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (ExpiredJwtException e) {
            LogUtil.warn("만료된 JWT token : {}", e.getMessage());
            throw GeneralException.of(ErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            LogUtil.warn("지원하지 않는 JWT token: {}", e.getMessage());
            throw GeneralException.of(ErrorCode.TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            LogUtil.warn("비어있는 JWT token : {}", e.getMessage());
            throw GeneralException.of(ErrorCode.TOKEN_NOT_FOUND);
        }
    }
}
