package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyPrice extends BaseEntity {

    private long dailyPriceId;
    private String productCode;

    private LocalDate priceDate;
    private long price;

    private Long priceChange;
    private BigDecimal priceChangeRate;
}
