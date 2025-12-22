package com.yachaerang.backend.api.bookmark.repository;

import com.yachaerang.backend.api.bookmark.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookmarkMapper {

    /**
     * 북마크 등록하기
     */
    int save(Bookmark bookmark);

    /**
     * 북마크 해제하기
     */
    int delete(@Param("memberId") Long memberId, @Param("articleId") Long articleId);
}
