package com.yachaerang.backend.global.auth.service;

import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;

    // 회원 가입
    public TokenResponseDto.ResultDto signup(TokenRequestDto.SignupDto signupDto) {
        return null;
    }

    // 로그인
    public TokenResponseDto.ResultDto login(TokenRequestDto.LoginDto loginDto) {
        return null;
    }

    // 로그아웃
    public void logout() {
        return;
    }

    // Access Token 재발급
    public String reissue(String refreshToken) {
        return null;
    }
}
