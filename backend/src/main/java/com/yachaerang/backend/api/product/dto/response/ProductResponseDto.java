package com.yachaerang.backend.api.product.dto.response;

import lombok.*;

public class ProductResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private String itemName;
        private String itemCode;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubItemDto {
        private String name;
        private String productCode;
        private String itemName;
        private String itemCode;
        private String kindName;
        private String kindCode;
        private String productRank;
        private String rankCode;
    }
}
