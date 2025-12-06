package com.yachaerang.backend.api.article.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article extends BaseEntity {

    private Long id;

    private String title;

    private String content;

    private String url;

    private List<Tag> tagList;
}
