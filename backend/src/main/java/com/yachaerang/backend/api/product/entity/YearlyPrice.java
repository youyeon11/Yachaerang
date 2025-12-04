package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YearlyPrice extends BaseEntity {

    private Long yearlyPriceId;
    private String productCode;

    private Integer priceYear;
    private Integer priceMonth;

    private Double avgPrice;
    private Long minPrice;
    private Long maxPrice;

    private Integer priceCount;
}
