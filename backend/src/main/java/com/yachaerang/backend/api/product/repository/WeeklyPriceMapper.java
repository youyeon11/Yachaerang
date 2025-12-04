package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.WeeklyPriceResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WeeklyPriceMapper {

    /*
    startDate와 endDate 기간 동안의 주차에 대하여 반환
     */
    List<WeeklyPriceResponseDto.PriceRecordDto> getPriceDuration(
            @Param("productCode") String productCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
            );
}
