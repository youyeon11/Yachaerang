package com.yachaerang.backend.api.product.dto.response;

import lombok.*;

import java.math.BigDecimal;

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
        private Long priceChange;
        private BigDecimal priceChangeRate;
    }
}
