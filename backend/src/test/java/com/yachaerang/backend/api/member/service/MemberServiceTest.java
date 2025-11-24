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

        // when

        // then
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void 프로필수정_성공() {
        // given

        // when

        // then
    }
}