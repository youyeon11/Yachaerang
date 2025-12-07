package com.yachaerang.backend.api.article.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

    private Long id;

    private String name;

    private Long articleId;
}
