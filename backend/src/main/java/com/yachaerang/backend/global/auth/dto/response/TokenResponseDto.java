package com.yachaerang.backend.global.auth.dto.response;

import lombok.*;

public class TokenResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDto {
        private String accessToken;
        private String refreshToken;
    }
}
