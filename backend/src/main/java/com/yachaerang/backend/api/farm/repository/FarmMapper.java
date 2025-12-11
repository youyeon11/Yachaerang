package com.yachaerang.backend.api.farm.repository;

import com.yachaerang.backend.api.farm.entity.Farm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FarmMapper {

    /**
     * 저장
     */
    int save(Farm farm);

    /**
     * memberId를 기반으로 조회하기
     */
    Farm findByMemberId(@Param("memberId") Long memberId);

    /**
     * 수정하기
     */
    int updateFarm(Farm farm);

    /**
     * 평가 결과 수정하기
     */
    int updateEvaluation(
            @Param("id") Long id,
            @Param("grade") String grade,
            @Param("comment") String comment
    );

    /**
     * grade와 comment가 채워지지 않은 것들을 조회하기
     */
    List<Farm> findFarmsWithMissingEvaluation();
}
