package com.yachaerang.backend.api.product.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class Category extends BaseEntity {

    private Long id;

    private String name;

    private String apiCategoryCode;

    private List<ProductCategory> ProductCategoryList;
}
