package com.yachaerang.backend.api.favorite.repository;

import com.yachaerang.backend.api.favorite.dto.response.FavoriteResponseDto;
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

    /**
     * 조건에 해당하는 관심 대시보드 조회
     * @param memberId : 저장한 주체
     * @param productCode : 대시보드 대상
     * @param periodType : 기간
     * @return
     */
    FavoriteResponseDto.ReadDto findByMemberIdAndProductCode(
            @Param("memberId") Long memberId,
            @Param("productCode") String productCode,
            @Param("periodType") String periodType
    );

    /*
    전체 조회하기
     */
    List<FavoriteResponseDto.ReadDto> findAllByMemberId(@Param("memberId") Long memberId);
}
