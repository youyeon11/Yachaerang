package com.yachaerang.backend.api.article.repository;

import com.yachaerang.backend.api.article.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    Article findById(@Param("id") int id);

    List<Article> findAll();

    List<Article> findAllWithTags();

    int insert(Article article);

    int update(Article article);

    int delete(@Param("id") int id);
}
