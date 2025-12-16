package com.yachaerang.backend.infrastructure.smtp.controller;

import com.yachaerang.backend.global.response.ApiResponse;
import com.yachaerang.backend.global.response.SuccessCode;
import com.yachaerang.backend.infrastructure.smtp.dto.request.MailRequestDto;
import com.yachaerang.backend.infrastructure.smtp.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mails")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    /*
    이메일 인증
     */
    @PostMapping("")
    public void mailSend(@RequestBody MailRequestDto.MailRequest request) {
        mailService.sendMail(request);
    }

    /*
    코드 유효성 검증
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<?>> verifyCode(@RequestBody MailRequestDto.VerificationRequest request) {
        boolean isVerified = mailService.isVerified(request);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessCode.OK, isVerified)
        );
    }

    /*
    비밀번호 초기화를 위한 이메일 검증
     */
    @PostMapping("/password/send-code")
    public void sendCodeForPassword(
            @RequestBody MailRequestDto.MailRequest request) {
        mailService.sendMail(request);
    }

    /*
    인증 코드 검증과 임시 비밀번호 발급
     */
    @PostMapping("/password/reset")
    public void resetPassword(
            @RequestBody MailRequestDto.VerificationRequest request
    ) {
        mailService.verifyAndSendTempPassword(request);
    }
}
