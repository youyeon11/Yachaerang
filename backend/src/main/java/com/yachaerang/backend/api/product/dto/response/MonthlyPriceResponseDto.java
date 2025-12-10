package com.yachaerang.backend.api.product.dto.response;

import lombok.*;

public class MonthlyPriceResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        private int priceYear;
        private int priceMonth;
        private Long avgPrice;
        private Long minPrice;
        private Long maxPrice;
    }
}
