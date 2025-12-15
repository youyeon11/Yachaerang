package com.yachaerang.backend.api.member.controller;

import com.yachaerang.backend.api.member.dto.request.MemberRequestDto;
import com.yachaerang.backend.api.member.dto.response.MemberResponseDto;
import com.yachaerang.backend.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadImage(
            @RequestHeader("Authorization") String token,
            @RequestPart("file")MultipartFile file
            ) {
        memberService.uploadProfileImage(file);
    }
}
