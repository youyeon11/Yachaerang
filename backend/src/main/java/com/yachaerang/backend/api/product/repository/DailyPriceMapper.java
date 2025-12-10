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

    /*
    특정날짜를 기준으로 가격 내림차순 정렬을 기반으로 보여주기
     */
    List<DailyPriceResponseDto.RankDto> getPricesDescending(
            @Param("priceDate") LocalDate priceDate
    );

    /*
    특정날짜를 기준으로 가격 오름차순 정렬을 기반으로 보여주기
     */
    List<DailyPriceResponseDto.RankDto> getPricesAscending(
            @Param("priceDate") LocalDate priceDate
    );
}
