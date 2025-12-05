package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.YearlyPriceResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YearlyPriceMapper {

    /*
    입력받은 연도들에 대하여 확인
     */
    List<YearlyPriceResponseDto.PriceDto> getPriceDuration(
            @Param("productCode") String productCode,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear
    );

    /*
    특정 연도에 대한 여려 수치 비교
     */
    YearlyPriceResponseDto.PriceDto getPriceSpecification(
            @Param("productCode") String productCode,
            @Param("year") Integer year
    );
}
