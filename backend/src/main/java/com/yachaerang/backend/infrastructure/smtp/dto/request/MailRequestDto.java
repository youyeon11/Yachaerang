package com.yachaerang.backend.infrastructure.smtp.dto.request;

import lombok.*;

public class MailRequestDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailRequest {
        private String mail;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationRequest {
        private String mail;
        private String code;
    }
}
