package com.yachaerang.backend.api.product.dto.response;

import lombok.*;

import java.time.LocalDate;

public class DailyPriceResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRecordDto {
        private LocalDate priceDate;
        private Long price;
    }
}
