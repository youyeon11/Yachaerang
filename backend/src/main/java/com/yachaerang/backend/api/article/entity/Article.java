package com.yachaerang.backend.api.article.entity;

import com.yachaerang.backend.api.common.BaseEntity;

import java.util.List;

public class Article extends BaseEntity {

    private Long id;

    private String title;

    private String content;

    private List<Tag> tagList;
}
