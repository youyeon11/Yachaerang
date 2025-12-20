package com.yachaerang.backend.api.product.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeeklyPriceResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRecordDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private Long avgPrice;
        private Long minPrice;
        private Long maxPrice;
        private Long priceChange;
        private BigDecimal priceChangeRate;
    }
}
