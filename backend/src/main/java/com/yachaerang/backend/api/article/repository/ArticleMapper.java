package com.yachaerang.backend.api.article.repository;

import com.yachaerang.backend.api.article.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /*
    특정 게시글 조회
     */
    Article findById(@Param("id") Long id);


    /**
     * 페이지로 Article 조회 (페이지의 단위 설정)
     * @param limit : 몇 개 씩 보여줄 것인지 설정 -> size와 동일
     * @param offset : 얼마나 건너뛸 것인지 설정 -> 몇 page인지에 따라 limit 을 (page-1)만큼 건너뜀
     * @return
     */
    List<Article> findAllWithPagination(
            @Param("limit") int limit, @Param("offset") int offset
    );

    /*
    모든 Article 개수 조회
     */
    Long countAll();
}
