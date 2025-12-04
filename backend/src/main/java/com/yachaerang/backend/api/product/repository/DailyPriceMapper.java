package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.DailyPriceResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyPriceMapper {

    /*
    priceDate가 startDate에서 endDate까지인 것에 대하여 반환
     */
    List<DailyPriceResponseDto.PriceRecordDto> getPriceDuration(
            @Param("productCode") String productCode,
            @Param("startDate")LocalDate startDate,
            @Param("endDate")LocalDate endDate
            );
}
