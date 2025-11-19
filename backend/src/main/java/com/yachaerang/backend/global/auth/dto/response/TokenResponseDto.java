package com.yachaerang.backend.global.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class TokenResponseDto {

    @Getter
    @Setter
    @Builder
    public static class ResultDto {
        private String accessToken;
        private String refreshToken;
    }
}
