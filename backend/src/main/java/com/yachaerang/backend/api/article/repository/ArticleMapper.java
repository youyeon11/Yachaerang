package com.yachaerang.backend.api.article.repository;

import com.yachaerang.backend.api.article.dto.request.ArticleRequestDto;
import com.yachaerang.backend.api.article.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /*
    특정 게시글 조회
     */
    Article findById(@Param("id") Long id);

    /*
    Article 페이지로 조회
     */
    List<Article> findAllWithPagination(ArticleRequestDto.PageDto pageRequest);

    /*
    모든 Article 개수 조회
     */
    Long countAll();
}
