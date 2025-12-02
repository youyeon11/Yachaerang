package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.Data;

@Data
public class Product extends BaseEntity {

    private Long id;

    private String name;

    private String productCode;

    // API Mapping
    private String itemName;
    private String itemCode;
    private String kindName;
    private String kindCode;
    private String productRank;

    private String unit;
    private String origin;
    private String imageUrl;
}
