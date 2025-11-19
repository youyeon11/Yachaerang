package com.yachaerang.backend.global.auth.controller;

import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import com.yachaerang.backend.global.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public void signup(
            @RequestBody TokenRequestDto.SignupDto signupDto
            ) throws Exception {
        authService.signup(signupDto);
        return;
    }

    @PostMapping("/login")
    public TokenResponseDto.ResultDto signup(
            @RequestBody TokenRequestDto.LoginDto loginDto,
            HttpServletResponse response
    ) throws Exception {
        return authService.login(loginDto, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response, String token) throws Exception {
        authService.logout(response, token);
    }

    @PostMapping("/reissue")
    public void reissue(String refreshToken) throws Exception {
        authService.reissue(refreshToken);
    }
}
