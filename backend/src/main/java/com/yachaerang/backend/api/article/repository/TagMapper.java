package com.yachaerang.backend.api.article.repository;

import com.yachaerang.backend.api.article.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {

    /*
    여러 Article의 Tag 조회
     */
    List<Tag> findByArticleIdList(
            @Param("articleIdList") List<Long> articleIdList
    );

    /*
    특정 Article의 Tag Name 조회
     */
    List<String> findNameByArticleId(
            @Param("articleId") Long articleId
    );
}
