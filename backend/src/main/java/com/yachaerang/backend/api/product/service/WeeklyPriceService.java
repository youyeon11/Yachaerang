package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.WeeklyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.WeeklyPriceMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeeklyPriceService {

    private final WeeklyPriceMapper weeklyPriceMapper;

    public List<WeeklyPriceResponseDto.PriceRecordDto> getPriceDuration(
            String productCode, LocalDate startDate, LocalDate endDate
    ) {
        // startDate > endDate
        if (endDate.isBefore(startDate)) {
            throw GeneralException.of(ErrorCode.WRONG_REQUEST_DATE);
        }
        LocalDate today = LocalDate.now();
        if (endDate.isAfter(today)) {
            throw GeneralException.of(ErrorCode.WRONG_REQUEST_DATE);
        }

        LocalDate weekStart = startDate.plusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate weekEnd = endDate.minusWeeks(1).with(DayOfWeek.SUNDAY);
        return weeklyPriceMapper.getPriceDuration(productCode, weekStart, weekEnd);
    }
}
