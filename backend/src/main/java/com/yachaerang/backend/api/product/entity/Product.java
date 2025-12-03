package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    private Long productId;

    private String name;
    private String productCode;

    private String itemName;
    private String itemCode;

    private String kindName;
    private String kindCode;

    private String productRank;
    private String rankCode;

    private String unit;
    private String origin;
    private String imageUrl;
}