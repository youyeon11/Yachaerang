package com.yachaerang.backend.api.farm.repository;

import com.yachaerang.backend.api.farm.entity.Farm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
            @Param("farmType") String farmType,
            @Param("comment") String comment
    );

    /**
     * 회원 ID로 농장 조회 + for update를 통한 배타적 락
     */
    Farm findByMemberIdForUpdate(@Param("memberId") Long memberId);
}
