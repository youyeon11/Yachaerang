package com.yachaerang.backend.global.auth.controller;

import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import com.yachaerang.backend.global.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public TokenResponseDto.ResultDto signup(
            @RequestBody TokenRequestDto.SignupDto signupDto
            ) throws Exception {
        return authService.signup(signupDto);
    }

    @PostMapping("/login")
    public TokenResponseDto.ResultDto signup(
            @RequestBody TokenRequestDto.LoginDto loginDto
    ) throws Exception {
        return authService.login(loginDto);
    }

    @PostMapping("/logout")
    public void logout() throws Exception {
        authService.logout();
    }

    @PostMapping("/reissue")
    public void reissue(String refreshToken) throws Exception {
        authService.reissue(refreshToken);
    }
}
