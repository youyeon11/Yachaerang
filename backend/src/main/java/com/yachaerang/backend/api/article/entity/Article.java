package com.yachaerang.backend.api.article.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class Article extends BaseEntity {

    private Long id;

    private String title;

    private String content;

    private List<Tag> tagList;
}
