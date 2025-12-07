package com.yachaerang.backend.api.member.service;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.dto.request.MemberRequestDto;
import com.yachaerang.backend.api.member.dto.response.MemberResponseDto;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
                .imageUrl("default.png")
                .build();

        mypageRequestDto = MemberRequestDto.MyPageDto.builder()
                .name("test2")
                .nickname("test2")
                .imageUrl("update.png")
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
        assertThat(result.getImageUrl()).isEqualTo("default.png");

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
                .imageUrl(mypageRequestDto.getImageUrl())
                .build();
        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(1L);
        given(memberMapper.updateProfile(anyLong(), anyString(), anyString(), anyString())).willReturn(1);
        given(memberMapper.findById(1L)).willReturn(updateMember);

        // when
        MemberResponseDto.MyPageDto result = memberService.updateProfile(mypageRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("test2");
        assertThat(result.getNickname()).isEqualTo("test2");
        assertThat(result.getImageUrl()).isEqualTo("update.png");

        verify(authenticatedMemberProvider, times(1)).getCurrentMemberId();
        verify(memberMapper, times(1)).updateProfile(1L, "test2", "test2", "update.png");
        verify(memberMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("프로필 수정 실패 - 인증되지 않은 사용자")
    void 프로필수정_실패_인증되지않은사용자() {
        // given
        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(null);

        // when & then
        assertThatThrownBy(() -> memberService.updateProfile(mypageRequestDto))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException exception = (GeneralException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("프로필 수정 실패 시 INTERNAL_SERVER_ERROR 예외")
    void updateProfile_UpdateFailed_예외() {
        // given
        Long memberId = member.getId();

        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(memberId);
        given(memberMapper.updateProfile(
                memberId,
                mypageRequestDto.getName(),
                mypageRequestDto.getNickname(),
                mypageRequestDto.getImageUrl()
        )).willReturn(0); // 업데이트 실패

        // when & then
        assertThatThrownBy(() -> memberService.updateProfile(mypageRequestDto))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ge = (GeneralException) exception;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
                });

        then(memberMapper).should(never()).findById(any());
    }

    @Test
    @DisplayName("이름만 수정 성공")
    void 이름만_수정_성공() {
        // given
        Long memberId = member.getId();
        MemberRequestDto.MyPageDto request = MemberRequestDto.MyPageDto.builder()
                .name("새이름")
                .nickname(null)
                .imageUrl(null)
                .build();

        Member updatedMember = Member.builder()
                .id(memberId)
                .name("새이름")
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .build();

        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(memberId);
        given(memberMapper.updateProfile(memberId, "새이름", null, null)).willReturn(1);
        given(memberMapper.findById(memberId)).willReturn(updatedMember);

        // when
        MemberResponseDto.MyPageDto result = memberService.updateProfile(request);

        // then
        assertThat(result.getName()).isEqualTo("새이름");
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getImageUrl()).isEqualTo(member.getImageUrl());

        then(memberMapper).should().updateProfile(memberId, "새이름", null, null);
    }

    @Test
    @DisplayName("변경사항 없는 요청 시 EMPTY_MYPAGE_REQUEST 예외")
    void 변경사항_없는_요청시_예외 () {
        // given
        Long memberId = member.getId();
        MemberRequestDto.MyPageDto emptyRequestDto = MemberRequestDto.MyPageDto.builder().build();
        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(memberId);

        // when & then
        assertThatThrownBy(() -> memberService.updateProfile(emptyRequestDto))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ge = (GeneralException) exception;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.EMPTY_MYPAGE_REQUEST);
                });

        then(memberMapper).should(never()).updateProfile(any(), any(), any(), any());
        then(memberMapper).should(never()).findById(any());
    }
}