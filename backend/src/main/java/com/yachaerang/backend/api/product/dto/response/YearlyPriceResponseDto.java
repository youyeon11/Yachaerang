package com.yachaerang.backend.api.product.dto.response;

import lombok.*;

public class YearlyPriceResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        private int priceYear;
        private Long avgPrice;
        private Long minPrice;
        private Long maxPrice;
        private Long startPrice;
        private Long endPrice;
    }
}
