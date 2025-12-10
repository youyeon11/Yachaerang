package com.yachaerang.backend.global.auth.jwt;

import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks private JwtTokenProvider jwtTokenProvider;

    @Mock private MemberMapper memberMapper;

    private static final String SECRET_KEY = "testSecretKeyForJwtTokenTestingPurpose123456";
    private static final long ACCESS_TOKEN_EXPIRATION = 3600L; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 604800L; // 7일

    private Member member;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationTime", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationTime", REFRESH_TOKEN_EXPIRATION);

        member = Member.builder()
                .memberCode("MEMBER-UUID-12345")
                .email("test@example.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Access Token 생성 성공")
    void generateAccessToken_성공() {
        // when
        String accessToken = jwtTokenProvider.generateAccessToken(member);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.split("\\.")).hasSize(3); // JWT 형식 검증
    }

    @Test
    @DisplayName("Refresh Token 생성 성공")
    void generateRefreshToken_성공() {
        // when
        String refreshToken = jwtTokenProvider.generateRefreshToken(member);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Access Token과 Refresh Token은 서로 다름")
    void accessAndRefreshToken_비교_성공() {
        // when
        String accessToken = jwtTokenProvider.generateAccessToken(member);
        String refreshToken = jwtTokenProvider.generateRefreshToken(member);

        // then
        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("유효한 토큰에서 MemberCode 추출 성공")
    void getMemberCodeFromToken_성공() throws Exception {
        // given
        String token = jwtTokenProvider.generateAccessToken(member);

        // when
        String memberCode = jwtTokenProvider.getMemberCodeFromToken(token);

        // then
        assertThat(memberCode).isEqualTo(member.getMemberCode());
    }

    @Test
    @DisplayName("잘못된 토큰으로 MemberCode 추출 시 예외")
    void getMemberCodeFromToken_InvalidToken_예외() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getMemberCodeFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_ValidToken_성공() throws Exception {
        // given
        String token = jwtTokenProvider.generateAccessToken(member);

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 TOKEN_EXPIRED 예외")
    void validateToken_ExpiredToken_ThrowsException() {
        // given
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000);
        String expiredToken = Jwts.builder()
                .setSubject(member.getMemberCode())
                .claim("roles", member.getRole().name())
                .setIssuedAt(new Date(now.getTime() - 10000))
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ge = (GeneralException) exception;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.TOKEN_EXPIRED);
                });
    }

    @Test
    @DisplayName("잘못된 서명의 토큰 검증 시 TOKEN_SIGNATURE_INVALID 예외")
    void validateToken_InvalidSignature_ThrowsException() {
        // given
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000); // 1초 전에 만료
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject(member.getMemberCode())
                .claim("roles", member.getRole().name())
                .setIssuedAt(new Date(now.getTime() - 10000))
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, "differentSecretKeyForTesting12345678901234567890")
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(tokenWithWrongSignature))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ge = (GeneralException) exception;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.TOKEN_SIGNATURE_INVALID);
                });
    }

    @Test
    @DisplayName("빈 토큰 검증 시 TOKEN_NOT_FOUND 예외")
    void validateToken_EmptyToken_ThrowsException() {
        // given
        String emptyToken = "";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(emptyToken))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ge = (GeneralException) exception;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.TOKEN_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("Authentication 객체 생성 성공")
    void getAuthentication_성공() throws Exception {
        // given
        String token = jwtTokenProvider.generateAccessToken(member);
        given(memberMapper.findByMemberCode(member.getMemberCode())).willReturn(member);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(member); // principal은 Member 객체
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly(member.getRole().name());
    }

    @Test
    @DisplayName("존재하지 않는 회원의 토큰으로 Authentication 생성 시 MEMBER_NOT_FOUND 예외")
    void getAuthentication_MemberNotFound_예외() {
        // given
        String token = jwtTokenProvider.generateAccessToken(member);
        given(memberMapper.findByMemberCode(member.getMemberCode())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(token))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ge = (GeneralException) exception;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
                });
    }


    @Test
    @DisplayName("ADMIN 역할의 Authentication")
    void ADIMIN_역할의_Authentication() throws Exception {
        // given
        Member adminMember = Member.builder()
                .memberCode("ADMIN-UUID-12345")
                .email("admin@example.com")
                .role(Role.ROLE_ADMIN)
                .build();

        String token = jwtTokenProvider.generateAccessToken(adminMember);
        given(memberMapper.findByMemberCode(adminMember.getMemberCode())).willReturn(adminMember);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

}