package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.Data;

@Data
public class ProductCategory extends BaseEntity {
    private Long id;
    private Category category;
    private Product product;
}
