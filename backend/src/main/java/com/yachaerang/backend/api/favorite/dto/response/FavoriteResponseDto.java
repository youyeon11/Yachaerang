package com.yachaerang.backend.api.favorite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FavoriteResponseDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterDto {
        private Long favoriteId;
        private String productCode;
        private String periodType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReadDto {
        private Long favoriteId;
        private String itemCode;
        private String itemName;
        private String productCode;
        private String productName;
        private String periodType;
    }
}
