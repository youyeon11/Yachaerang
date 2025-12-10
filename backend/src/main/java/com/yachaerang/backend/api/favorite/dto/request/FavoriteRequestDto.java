package com.yachaerang.backend.api.favorite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FavoriteRequestDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RegisterDto {
        private String productCode;
        private String periodType;
    }
}
