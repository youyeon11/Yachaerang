package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class Product extends BaseEntity {

    private Long id;

    private String name;

    private String productCode;

    // API Mapping
    private String itemCode;
    private String itemCategoryCode;
    private String kindCode;
    private String productRankCode;
    private String countryCode;

    private String unit;
    private String standardWeight;
    private String grade;
    private String origin;
    private String imageUrl;

    private List<Category> categoryList;
}
