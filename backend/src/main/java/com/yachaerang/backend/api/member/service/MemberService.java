package com.yachaerang.backend.api.member.service;

import com.yachaerang.backend.api.member.dto.request.MemberRequestDto;
import com.yachaerang.backend.api.member.dto.response.MemberResponseDto;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberMapper memberMapper;
    private final AuthenticatedMemberProvider authenticatedMemberProvider;

    /*
    나의 정보 조회하기
     */
    @Transactional(readOnly = true)
    public MemberResponseDto.MyPageDto getProfile() {
        // Member 확인
        Member member = authenticatedMemberProvider.getMemberByContextHolder();

        MemberResponseDto.MyPageDto myPageDto = MemberResponseDto.MyPageDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .build();
        return myPageDto;
    }

    /*
    나의 정보 수정하기
     */
    @Transactional
    public MemberResponseDto.MyPageDto updateProfile(MemberRequestDto.MyPageDto myPageDto) {
        // Member 확인
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        if (memberId == null) {
            throw new GeneralException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (myPageDto.getName() == null && myPageDto.getNickname() == null && myPageDto.getImageUrl() == null) {
            throw new GeneralException(ErrorCode.EMPTY_MYPAGE_REQUEST);
        }

        int result = memberMapper.updateProfile(
                memberId,
                myPageDto.getName(),
                myPageDto.getNickname(),
                myPageDto.getImageUrl()
        );

        if (result == 0) {
            throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // return확인
        Member member = memberMapper.findById(memberId);
        return MemberResponseDto.MyPageDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .build();
    }
}
