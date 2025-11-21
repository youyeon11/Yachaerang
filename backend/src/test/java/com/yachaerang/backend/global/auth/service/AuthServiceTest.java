package com.yachaerang.backend.global.auth.service;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import com.yachaerang.backend.global.auth.jwt.JwtTokenProvider;
import com.yachaerang.backend.global.auth.util.RefreshTokenUtil;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private RefreshTokenUtil refreshTokenUtil;

    @InjectMocks
    private AuthService authService;

    // HttpServlet
    @Mock
    private HttpServletResponse response;

    /*
    테스트 객체 선언
     */
    private TokenRequestDto.SignupDto signupDto;
    private TokenRequestDto.LoginDto loginDto;
    private Member member;

    @BeforeEach
    void setUp() {
        signupDto = TokenRequestDto.SignupDto.builder()
                .name("test1")
                .nickname("test1")
                .password("test1")
                .email("test1@test.com")
                .build();

        loginDto = TokenRequestDto.LoginDto.builder()
                .email("test1@test.com")
                .password("test1")
                .build();

        member = Member.builder()
                .nickname("test1")
                .password("test1")
                .email("test1@test.com")
                .memberStatus(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() throws Exception {
        // given
        given(memberMapper.findByEmail(signupDto.getEmail())).willReturn(null);
        given(bCryptPasswordEncoder.encode(signupDto.getPassword())).willReturn("test1");

        // when
        authService.signup(signupDto);

        // then
        // 실제 저장한 값을 캡쳐해서 사용
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberMapper, times(1)).findByEmail(signupDto.getEmail());
        verify(bCryptPasswordEncoder, times(1)).encode(signupDto.getPassword());
        verify(memberMapper, times(1)).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo(signupDto.getName());
        assertThat(savedMember.getEmail()).isEqualTo(signupDto.getEmail());
        assertThat(savedMember.getPassword()).isEqualTo("test1");
        assertThat(savedMember.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(savedMember.getMemberStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("이미 존재하는 회원일 때의 실패")
    void 이미_존재하는_회원() throws Exception {
        // given
        given(memberMapper.findByEmail(signupDto.getEmail())).willReturn(member);

        // when & then
        assertThatThrownBy(() -> authService.signup(signupDto))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException exception = (GeneralException) e;
                    assertThat(exception.getErrorCode())
                            .isEqualTo(ErrorCode.MEMBER_ALREADY_EXISTS);
                });

        // 각각의 실행 횟수 확인
        verify(memberMapper, times(1)).findByEmail(signupDto.getEmail());
        verify(memberMapper, times(0)).save(member);
    }

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() {
        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        given(memberMapper.findByEmail(loginDto.getEmail())).willReturn(member);
        // BCryptEncoding
        given(bCryptPasswordEncoder.matches(loginDto.getPassword(), "test1")).willReturn(true);
        // header
        given(jwtTokenProvider.generateAccessToken(member)).willReturn(accessToken);
        given(jwtTokenProvider.generateRefreshToken(member)).willReturn(refreshToken);

        // when
        TokenResponseDto.ResultDto result = authService.login(loginDto, response);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);

        // 몇번씩 실행됐는지 검증
        verify(memberMapper, times(1)).findByEmail(loginDto.getEmail());
        verify(bCryptPasswordEncoder, times(1)).matches(loginDto.getPassword(), member.getPassword());
        verify(jwtTokenProvider, times(1)).generateAccessToken(member);
        verify(jwtTokenProvider, times(1)).generateRefreshToken(member);
        verify(refreshTokenUtil, times(1)).saveRefreshToken(member.getMemberCode(), refreshToken);
        verify(refreshTokenUtil, times(1)).addRefreshTokenCookie(response, member.getMemberCode(), refreshToken);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void 로그인실패_비밀번호불일치() {
        // given
        given(memberMapper.findByEmail(loginDto.getEmail())).willReturn(member);
        given(bCryptPasswordEncoder.matches(loginDto.getPassword(), member.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto, response))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException exception = (GeneralException) e;
                    assertThat(exception.getErrorCode())
                            .isEqualTo(ErrorCode.UNMATCHED_PASSWORD);
                });

        verify(memberMapper, times(1)).findByEmail(loginDto.getEmail());
        verify(bCryptPasswordEncoder, times(1)).matches(loginDto.getPassword(), member.getPassword());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }
}