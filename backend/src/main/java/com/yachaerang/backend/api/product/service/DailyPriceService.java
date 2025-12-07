package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.DailyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.DailyPriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyPriceService {

    private final DailyPriceMapper dailyPriceMapper;

    /*
    해당 기간 동안의 가격 전부 가져오기
     */
    public List<DailyPriceResponseDto.PriceRecordDto> getDailyPrice(
            String productCode, LocalDate startDate, LocalDate endDate) {
        return dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);
    }

    /*
    어제자를 기준으로 가격이 높은 것부터 보여주기
     */
    public List<DailyPriceResponseDto.RankDto> getHighPriceRank() {
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime yesterday = today.minusDays(1);
        return dailyPriceMapper.getPricesDescending(yesterday.toLocalDate());
    }

    /*
    어제자를 기준으로 가격이 낮은 것부터 보여주기
     */
    public List<DailyPriceResponseDto.RankDto> getLowPriceRank() {
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime yesterday = today.minusDays(1);
        return dailyPriceMapper.getPricesAscending(yesterday.toLocalDate());
    }
}
