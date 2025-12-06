package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MonthlyPriceMapper {

    /*
    년도와 일을 입력 받으면 그에 맞는 값들 반환
     */
    List<MonthlyPriceResponseDto.PriceDto> getPriceDuration(
            @Param("productCode") String productCode,
            @Param("startYear") Integer startYear, @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear, @Param("endMonth") Integer endMonth
    );
}
