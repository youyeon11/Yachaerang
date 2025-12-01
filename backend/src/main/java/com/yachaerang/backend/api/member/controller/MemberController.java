package com.yachaerang.backend.api.member.controller;

import com.yachaerang.backend.api.member.dto.request.MemberRequestDto;
import com.yachaerang.backend.api.member.dto.response.MemberResponseDto;
import com.yachaerang.backend.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /*
    마이페이지 조회
     */
    @GetMapping("")
    public MemberResponseDto.MyPageDto getProfile(
            @RequestHeader("Authorization") String accessToken
    ) {
        return memberService.getProfile();
    }

    /*
    마이페이지 수정
     */
    @PatchMapping("")
    public MemberResponseDto.MyPageDto patchProfile(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MemberRequestDto.MyPageDto myPageDto
            ) {
        return memberService.updateProfile(myPageDto);
    }
}
