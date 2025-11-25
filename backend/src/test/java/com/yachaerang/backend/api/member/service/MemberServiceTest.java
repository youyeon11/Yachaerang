package com.yachaerang.backend.api.member.service;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.dto.request.MemberRequestDto;
import com.yachaerang.backend.api.member.dto.response.MemberResponseDto;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import com.yachaerang.backend.global.auth.jwt.JwtTokenProvider;
import com.yachaerang.backend.global.auth.util.RefreshTokenUtil;
import com.yachaerang.backend.global.auth.util.UuidGenerator;
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
class MemberServiceTest {

    @Mock
    MemberMapper memberMapper;

    @Mock
    AuthenticatedMemberProvider authenticatedMemberProvider;

    @InjectMocks
    MemberService memberService;

    /*
    테스트 객체 선언
     */
    private MemberRequestDto.MyPageDto mypageRequestDto;
    private MemberResponseDto.MyPageDto mypageResponseDto;
    private Member member;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .name("test")
                .nickname("test")
                .role(Role.ROLE_USER)
                .memberStatus(MemberStatus.ACTIVE)
                .build();

        mypageRequestDto = MemberRequestDto.MyPageDto.builder()
                .name("test2")
                .nickname("test2")
                .build();
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void 프로필조회_성공() {
        // given
        given(authenticatedMemberProvider.getMemberByContextHolder()).willReturn(member);

        // when
        MemberResponseDto.MyPageDto result = memberService.getProfile();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getNickname()).isEqualTo("test");

        verify(authenticatedMemberProvider, times(1)).getMemberByContextHolder();
    }

    @Test
    @DisplayName("프로필 조회 실패 - 인증되지 않은 사용자")
    void 프로필조회_실패_인증되지않은사용자() {
        // given
        given(authenticatedMemberProvider.getMemberByContextHolder()).willThrow(GeneralException.of(ErrorCode.UNAUTHORIZED_ACCESS));

        // when & then
        assertThatThrownBy(() -> memberService.getProfile())
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException exception = (GeneralException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_ACCESS);
                });
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void 프로필수정_성공() {
        // given
        Member updateMember = Member.builder()
                .id(1L)
                .email("test@example.com")
                .name(mypageRequestDto.getName())
                .nickname(mypageRequestDto.getNickname())
                .build();
        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(1L);
        given(memberMapper.updateProfile(anyLong(), anyString(), anyString())).willReturn(1);
        given(memberMapper.findById(1L)).willReturn(updateMember);

        // when
        MemberResponseDto.MyPageDto result = memberService.updateProfile(mypageRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("test2");
        assertThat(result.getNickname()).isEqualTo("test2");

        verify(authenticatedMemberProvider, times(1)).getCurrentMemberId();
        verify(memberMapper, times(1)).updateProfile(1L, "test2", "test2");
        verify(memberMapper, times(1)).findById(1L);
    }
}