package com.yachaerang.backend.api.favorite.repository;

import com.yachaerang.backend.api.favorite.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    /*
    등록하기
     */
    int save(Favorite favorite);

    /*
    해제하기
     */
    int deleteByIdAndMemberId(
            @Param("favoriteId") Long favoriteId,
            @Param("memberId") Long memberId
    );

    /*
    찾기
     */
    Favorite findByMemberIdAndProductCode(
            @Param("memberId") Long memberId,
            @Param("productCode") String productCode,
            @Param("periodType") String periodType
    );

    /*
    전체 조회하기
     */
    List<Favorite> findAllByMemberId(@Param("memberId") Long memberId);
}
