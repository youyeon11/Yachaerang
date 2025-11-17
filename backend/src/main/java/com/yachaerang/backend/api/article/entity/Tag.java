package com.yachaerang.backend.api.article.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.Data;

@Data
public class Tag extends BaseEntity {

    private Long id;

    private String name;

    private Article article;
}
